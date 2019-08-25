package com.yyxnb.yyxarch.interfaces

/**
 * 状态栏透明
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class StatusBarTranslucent(val value: Boolean = true)
