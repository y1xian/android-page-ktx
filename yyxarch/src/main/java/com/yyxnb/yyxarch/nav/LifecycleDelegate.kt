package com.yyxnb.yyxarch.nav

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner

/**
 * Lifecycle
 */
class LifecycleDelegate(lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    private val immediateLifecycleDelegate: ImmediateLifecycleDelegate = ImmediateLifecycleDelegate(lifecycleOwner)
    private val deferredLifecycleDelegate: DeferredLifecycleDelegate = DeferredLifecycleDelegate(lifecycleOwner)

    @JvmOverloads
    fun scheduleTaskAtStarted(runnable: Runnable, interval: Long = 100L) {
        if (interval >= 100L) {
            deferredLifecycleDelegate.scheduleTaskAtStarted(runnable, interval)
        } else {
            immediateLifecycleDelegate.scheduleTaskAtStarted(runnable)
        }
    }

}
