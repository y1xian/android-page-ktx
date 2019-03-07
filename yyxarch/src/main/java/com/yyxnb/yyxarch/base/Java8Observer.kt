package com.yyxnb.yyxarch.base

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.util.Log

object Java8Observer : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        Log.d("---", "onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d("---", "onStart")
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d("---", "onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d("---", "onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d("---", "onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d("---", "onDestroy")
    }

}