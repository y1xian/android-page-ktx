package com.yyxnb.yyxarch.annotation;

import android.support.annotation.IntDef;

import com.yyxnb.yyxarch.base.BaseFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({BaseFragment.RESULT_OK, BaseFragment.RESULT_CANCELED})
@Retention(RetentionPolicy.SOURCE)
public @interface ResultCode {
}