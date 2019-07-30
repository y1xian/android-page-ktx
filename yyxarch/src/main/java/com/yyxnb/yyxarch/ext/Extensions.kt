package com.yyxnb.yyxarch.ext

import com.yyxnb.yyxarch.http.exception.ApiException
import com.yyxnb.yyxarch.utils.log.LogUtils
import java.lang.Exception

inline fun tryCatch(tryBlock: () -> Unit, catchBlock: (Exception) -> Unit = {}) {
    try {
        tryBlock()
    } catch (t: Exception) {
        LogUtils.e(ApiException.handleException(t).message)
        catchBlock(t)
    }
}