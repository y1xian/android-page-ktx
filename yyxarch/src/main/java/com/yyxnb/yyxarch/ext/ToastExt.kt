package com.yyxnb.yyxarch.ext

import android.content.Context
import com.yyxnb.yyxarch.utils.ToastUtils

infix fun Context.toast(value: String) = ToastUtils.normal(value)
