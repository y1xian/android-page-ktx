package com.yyxnb.yyxarch.annotation

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@IntDef(LceeStatus.Loading, LceeStatus.Content, LceeStatus.Empty, LceeStatus.Error)
@Retention(RetentionPolicy.SOURCE)
annotation class LceeStatus {
    companion object {
        const val Loading = 1
        const val Content = 2
        const val Empty = 3
        const val Error = 4
    }
}
