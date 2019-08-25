package com.yyxnb.yyxarch.interfaces

/**
 * tag标识
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class TagValue(val value: String = "")
