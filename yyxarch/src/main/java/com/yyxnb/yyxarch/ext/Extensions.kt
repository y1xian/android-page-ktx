package com.yyxnb.yyxarch.ext

inline fun tryCatch(tryBlock: () -> Unit, catchBlock: (Exception) -> Unit = {}) {
    try {
        tryBlock()
    } catch (t: Exception) {
        catchBlock(t)
    }
}