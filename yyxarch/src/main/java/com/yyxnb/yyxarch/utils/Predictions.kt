package com.yyxnb.yyxarch.utils

import android.text.TextUtils
import java.io.Serializable

/**
 * 用来验证判断参数是否合法
 */
object Predictions : Serializable {


    @JvmOverloads
    fun notNull(o: Any?, msg: String = "Can't be null!") {
        if (o == null) {
            throw NullPointerException(msg)
        }
    }

    @JvmOverloads
    fun notEmpty(charSequence: CharSequence, msg: String = "Can't be empty!") {
        if (TextUtils.isEmpty(charSequence)) {
            throw IllegalArgumentException(msg)
        }
    }

    /**
     * Throws an [IllegalArgumentException] if called on a thread other than the main thread.
     */
    fun onMainThread() {
        if (!MainThread.isMainThread) {
            throw IllegalArgumentException("You must call this method on the main thread")
        }
    }

    /**
     * Throws an [IllegalArgumentException] if called on the main thread.
     */
    fun onBackgroundThread() {
        if (MainThread.isMainThread) {
            throw IllegalArgumentException("You must call this method on a background thread")
        }
    }

}