package com.yyxnb.yyxarch.utils

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.io.Serializable

/**
 * 解决 Handler 会导致内存泄露
 */
class HandlerLifecycle : Handler, LifecycleObserver, Serializable {

    private var lifecycleOwner: LifecycleOwner? = null

    constructor(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        addObserver()
    }

    constructor(callback: Handler.Callback, lifecycleOwner: LifecycleOwner) : super(callback) {
        this.lifecycleOwner = lifecycleOwner
        addObserver()
    }

    constructor(looper: Looper, lifecycleOwner: LifecycleOwner) : super(looper) {
        this.lifecycleOwner = lifecycleOwner
        addObserver()
    }

    constructor(looper: Looper, callback: Handler.Callback, lifecycleOwner: LifecycleOwner) : super(looper, callback) {
        this.lifecycleOwner = lifecycleOwner
        addObserver()
    }

    private fun addObserver() {
        lifecycleOwner!!.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        removeCallbacksAndMessages(null)
        lifecycleOwner!!.lifecycle.removeObserver(this)
    }
}