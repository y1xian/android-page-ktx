package com.yyxnb.yyxarch.ext

import android.content.Context
import android.widget.Toast

//fun Context.toast(value: String) = ToastUtils.normal(value)

private var toast: Toast? = null

/**
 * 吐司
 * @param value 内容
 */
fun Context.toast(value: String) {

    toast?.apply {
        setText(value)
    } ?: apply {
        toast = Toast.makeText(applicationContext, value, Toast.LENGTH_SHORT)
    }
    toast?.show()
}
