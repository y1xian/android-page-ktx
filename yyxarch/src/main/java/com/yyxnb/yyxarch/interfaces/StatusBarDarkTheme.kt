package com.yyxnb.yyxarch.interfaces

/**
 * 状态栏深色浅色切换
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class StatusBarDarkTheme(val value: BarStyle = BarStyle.LightContent)
