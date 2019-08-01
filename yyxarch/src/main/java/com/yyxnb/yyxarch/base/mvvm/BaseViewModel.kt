package com.yyxnb.yyxarch.base.mvvm

import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.*
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.ext.tryCatch
import com.yyxnb.yyxarch.http.exception.ApiException
import com.yyxnb.yyxarch.utils.log.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis


/**
 * 逻辑处理
 *
 * 负责数据处理和View层与Model层的交互。
 * ViewModel通过数据仓库Repository获取数据来源，处理来自View的事件命令，同时更新数据。
 * @author : yyx
 * @date ：2018/6/13
 */
abstract class BaseViewModel<S : BaseState, T : BaseRepository<*>>(private val initialState: S) : ViewModel(), DefaultLifecycleObserver {

    protected lateinit var mRepository: T

    init {
        mRepository = AppUtils.getNewInstance<T>(this, 1)!!

        viewModelScope.launch {
            //将StateChannel连接到LiveData
            Log.w("BaseViewModel---", "Connecting StateChannel to LiveData")
            stateStore.stateChannel.consumeEach { state -> _state.value = state }
        }
    }

    fun launchUI(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(Dispatchers.Main) {
        val time = measureTimeMillis {
            tryCatch({
                block()
            }, {
                LogUtils.e(ApiException.handleException(it).message)
            })
        }
        println("Completed in $time ms")
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        owner.lifecycle.addObserver(mRepository)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        owner.lifecycle.removeObserver(mRepository)
    }

    /**
     * The state store associated with this view model.
     * The state store manages synchronized accesses and mutations to state
     *
     * Initialized lazily because the initialState needs to be initialized by the subclass
     */
    protected val stateStore: StateStore<S> by lazy { StateStoreImpl(initialState) }

    /**
     * Internal backing field for the [LiveData] based state observable exposed to View objects
     */
    private val _state = MutableLiveData<S>()

    /**
     * The observable live data class to provide current state to views.
     * Activities and Fragments may subscribe to it to get notified of state updates.
     */
    val state: LiveData<S> = MediatorLiveData<S>().apply {
        addSource(_state, this::setValue)
    }

    /**
     * A convenience property to access the current state without having to observe it
     *
     * This state is not guaranteed to be the latest, because there might be other state mutation
     * blocks in queue in the state store.
     */
    val currentState: S
        get() = stateStore.state

    /**
     * The only method through which state mutation is allowed in subclasses.
     *
     * Dispatches an action to the actions channel. The channel reduces the action
     * and current state to a new state and sets the new value on [_state]
     *
     * @param reducer The state reducer to create a new state from the current state
     */

    protected fun setState(reducer: suspend S.() -> S) {
        stateStore.set(reducer)
    }

    protected fun withState(block: suspend (S) -> Unit) {
        stateStore.get(block)
    }

    @CallSuper
    override fun onCleared() {
        LogUtils.w("Clearing ViewModel")
        super.onCleared()
        stateStore.cleanup()
    }
}
