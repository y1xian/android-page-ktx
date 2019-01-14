package com.yyxnb.yyxarch.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({LceeStatus.Loading, LceeStatus.Content, LceeStatus.Empty, LceeStatus.Error})
@Retention(RetentionPolicy.SOURCE)
public @interface LceeStatus {
    int Loading = 1;
    int Content = 2;
    int Empty = 3;
    int Error = 4;
}
