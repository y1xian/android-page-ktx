package com.yyxnb.yyxarch.nav

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner

/**
 * Lifecycle
 */
class LifecycleDelegate(lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    private val immediateLifecycleDelegate: ImmediateLifecycleDelegate
    private val deferredLifecycleDelegate: DeferredLifecycleDelegate

    init {
        immediateLifecycleDelegate = ImmediateLifecycleDelegate(lifecycleOwner)
        deferredLifecycleDelegate = DeferredLifecycleDelegate(lifecycleOwner)
    }

    @JvmOverloads
    fun scheduleTaskAtStarted(runnable: Runnable, deferred: Boolean = false) {
        if (deferred) {
            deferredLifecycleDelegate.scheduleTaskAtStarted(runnable)
        } else {
            immediateLifecycleDelegate.scheduleTaskAtStarted(runnable)
        }
    }

}
