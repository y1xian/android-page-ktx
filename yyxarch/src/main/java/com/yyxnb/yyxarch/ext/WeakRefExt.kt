package com.yyxnb.yyxarch.ext

import java.lang.ref.WeakReference
import java.util.concurrent.CancellationException
import kotlin.coroutines.suspendCoroutine

class WeakRef<T> internal constructor(any: T) {

    private val weakRef = WeakReference(any)

    suspend operator fun invoke(): T {
        return suspendCoroutine {
            val ref = weakRef.get() ?: throw CancellationException()
            ref
        }
    }
}

fun <T : Any> T.weakReference() = WeakRef(this)