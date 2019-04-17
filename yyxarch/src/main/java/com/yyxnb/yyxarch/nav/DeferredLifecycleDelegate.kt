package com.yyxnb.yyxarch.nav

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Handler
import android.os.Looper
import java.util.*

class DeferredLifecycleDelegate(private val lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    private val tasks = LinkedList<Runnable>()

    private val handler = Handler(Looper.getMainLooper())

    private var executing: Boolean = false

    private val executeTask = Runnable {
        executing = false
        considerExecute()
    }

    internal val isAtLeastStarted: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

    private val lifecycle: Lifecycle
        get() = lifecycleOwner.lifecycle

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun scheduleTaskAtStarted(runnable: Runnable) {
        if (lifecycle.currentState != Lifecycle.State.DESTROYED) {
            assertMainThread()
            tasks.add(runnable)
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

    internal fun considerExecute() {
        if (isAtLeastStarted && !executing) {
            executing = true
            val runnable = tasks.poll()
            if (runnable != null) {
                runnable.run()
                handler.postDelayed(executeTask, INTERVAL)
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

        private val INTERVAL: Long = 100

        internal val isMainThread: Boolean
            get() = Looper.getMainLooper().thread === Thread.currentThread()
    }
}
