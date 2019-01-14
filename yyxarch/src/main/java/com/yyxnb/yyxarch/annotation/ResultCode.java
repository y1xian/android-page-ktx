package com.yyxnb.yyxarch.annotation;

import android.support.annotation.IntDef;

import com.yyxnb.yyxarch.base.NoFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Yan Zhenjie on 2017/1/15.
 */
@IntDef({NoFragment.RESULT_OK, NoFragment.RESULT_CANCELED})
@Retention(RetentionPolicy.SOURCE)
public @interface ResultCode {
}