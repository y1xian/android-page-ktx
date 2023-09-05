package com.yyxnb.android.fragment

import androidx.fragment.app.Fragment
import com.yyxnb.android.fragment.container.PageContainerFragment
import com.yyxnb.android.fragment.mode.LaunchMode
import com.yyxnb.android.fragment.mode.SingleTaskFragment
import com.yyxnb.android.fragment.mode.SingleTopFragment

private val Fragment.mode: LaunchMode
    get() = when (this) {
        is SingleTaskFragment -> LaunchMode.SINGLE_TASK
        is SingleTopFragment -> LaunchMode.SINGLE_TOP
        else -> LaunchMode.NORMAL
    }

private val Fragment.who: String?
    get() = runCatching { Fragment::class.java.getDeclaredField("mWho").apply {
        isAccessible = true
    }.get(this) as? String }.getOrNull()

val Fragment.generateFragmentTag: String
    get() = (this as? PageContainerFragment)?.fragmentTag
        ?: "$mode;${this.arguments?.uriOrNull ?: ""};${this::class.java.canonicalName};${hashCode()};${who}"