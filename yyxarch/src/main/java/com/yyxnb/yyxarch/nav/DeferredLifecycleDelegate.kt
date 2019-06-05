package com.yyxnb.yyxarch.nav

import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.yyxnb.yyxarch.utils.HandlerLifecycle
import java.util.*

class DeferredLifecycleDelegate(private val lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    private val tasks = LinkedList<Runnable>()

    private val handler = HandlerLifecycle(Looper.getMainLooper(), lifecycleOwner)

    private var executing: Boolean = false

    private var interval: Long = 100L

    private val executeTask = Runnable {
        executing = false
        considerExecute()
    }

    private val isAtLeastStarted: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

    private val lifecycle: Lifecycle
        get() = lifecycleOwner.lifecycle

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun scheduleTaskAtStarted(runnable: Runnable, _interval: Long) {
        if (lifecycle.currentState != Lifecycle.State.DESTROYED) {
            assertMainThread()
            tasks.add(runnable)
            interval = _interval
            considerExecute()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    internal fun onStateChange() {
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            handler.removeCallbacks(executeTask)
            tasks.clear()
            lifecycle.removeObserver(this)
        } else {
            considerExecute()
        }
    }

    private fun considerExecute() {
        if (isAtLeastStarted && !executing) {
            executing = true
            val runnable = tasks.poll()
            if (runnable != null) {
                runnable.run()
                handler.postDelayed(executeTask, interval)
            } else {
                executing = false
            }
        }
    }

    private fun assertMainThread() {
        if (!isMainThread) {
            throw IllegalStateException("you should perform the task at main thread.")
        }
    }

    companion object {

        internal val isMainThread: Boolean
            get() = Looper.getMainLooper().thread === Thread.currentThread()
    }
}
