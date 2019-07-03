package com.yyxnb.yyxarch.base.mvvm

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import java.util.*
import kotlin.coroutines.CoroutineContext

internal interface Action<S : BaseState>
internal inline class SetStateAction<S : BaseState>(val reducer: suspend S.() -> S) : Action<S>
internal inline class GetStateAction<S : BaseState>(val block: suspend (S) -> Unit) : Action<S>

/**
 * An Implementation of [StateStore] interface. This class is expected to be owned by a
 * [BaseViewModel] which calls [cleanup] when it is cleared
 *
 * @param initialState The initial state object with which the owning ViewModel was created
 */
internal class StateStoreImpl<S : BaseState>(
        initialState: S,
        override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()
) : StateStore<S>, CoroutineScope {

    /**
     * A [ConflatedBroadcastChannel] to expose the latest value of state to its
     * subscribers
     */
    override val stateChannel = ConflatedBroadcastChannel(initialState)

    /**
     * A convenience property to access the current value of state without using the state channel
     */
    override val state: S
        get() = stateChannel.value

    /**
     * An actor that processes each incoming [Action].
     *
     * All [SetStateAction] messages are processed immediately
     * All [GetStateAction] messages are enqueued and processed when the channel is empty
     *
     * This means that before any GetStateAction can be processed, the channel must be free of any
     * SetStateActions. Therefore, the block inside the GetStateAction is always guaranteed to receive
     * the latest state.
     */
    private val actionsActor = actor<Action<S>>(capacity = Channel.UNLIMITED) {

        val getStateQueue = ArrayDeque<suspend (S) -> Unit>()

        consumeEach { action ->
            when (action) {
                is SetStateAction -> {
                    //处理集状态块
                    Log.w("StateStoreImpl---", "Processing set-state block")
                    val newState = action.reducer(state)
                    //将新状态发送到通道
                    Log.w("StateStoreImpl---", "Sending new state to channel: $newState")
                    stateChannel.offer(newState)
                }
                is GetStateAction -> {
                    //正在处理获取状态块
                    Log.w("StateStoreImpl---", "Processing get-state block")
                    getStateQueue.offer(action.block)
                }
            }

            getStateQueue
                    .takeWhile { channel.isEmpty }
                    .map { block ->
                        block(state)
                        getStateQueue.removeFirst()
                    }
        }
    }

    /**
     * Send a [SetStateAction] to [actionsActor] to be processed immediately if the channel is empty,
     * or after all preceeding [SetStateAction] objects in it.
     *
     * @param reducer The reducer to produce a new state from the given state.
     */
    override fun set(reducer: suspend S.() -> S) {
        actionsActor.offer(SetStateAction(reducer))
    }

    /**
     * Send a [GetStateAction] to [actionsActor] to be processed after all preceeding [SetStateAction]
     * objects. This ensures that the state parameter passed to [block] is always the latest.
     *
     * @param block The action to be executed using the given state
     */
    override fun get(block: suspend (S) -> Unit) {
        actionsActor.offer(GetStateAction(block))
    }

    /**
     * Cleanup all resources of this state store.
     */
    override fun cleanup() {
        actionsActor.close()
        stateChannel.close()
        this.cancel() // Cancel coroutine scope
    }
}
