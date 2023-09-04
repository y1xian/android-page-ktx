package com.yyxnb.android.fragment.interceptor

open class InterruptedException(
    val interceptor: Interceptor, msg: String? = null
): RuntimeException("Interrupted By ${interceptor::class.java.simpleName} $msg")