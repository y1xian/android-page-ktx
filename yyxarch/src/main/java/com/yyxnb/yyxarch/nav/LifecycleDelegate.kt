package com.yyxnb.yyxarch.nav

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Lifecycle
 */
class LifecycleDelegate(lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    private val immediateLifecycleDelegate: ImmediateLifecycleDelegate = ImmediateLifecycleDelegate(lifecycleOwner)
    private val deferredLifecycleDelegate: DeferredLifecycleDelegate = DeferredLifecycleDelegate(lifecycleOwner)

    @JvmOverloads
    fun scheduleTaskAtStarted(runnable: Runnable, deferred: Boolean = false, interval: Long) {
        if (deferred) {
            deferredLifecycleDelegate.scheduleTaskAtStarted(runnable, interval)
        } else {
            immediateLifecycleDelegate.scheduleTaskAtStarted(runnable)
        }
    }

}
