package com.yyxnb.yyxarch.annotation

import android.support.annotation.IntDef

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@IntDef(YGravity.CENTER, YGravity.ABOVE, YGravity.BELOW, YGravity.ALIGN_TOP, YGravity.ALIGN_BOTTOM)
@Retention(RetentionPolicy.SOURCE)
annotation class YGravity {
    companion object {
        const val CENTER = 0
        const val ABOVE = 1
        const val BELOW = 2
        const val ALIGN_TOP = 3
        const val ALIGN_BOTTOM = 4
    }
}