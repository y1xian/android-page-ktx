package com.yyxnb.android.utils

import com.yyxnb.android.helper.FastClickHelper

fun Any.isFastClick(interval: Long = FastClickHelper.MIN_CLICK_DELAY_TIME, block: () -> Unit = {}) {
    if (FastClickHelper.isFastClick(interval)) {
        block()
    }
}

fun Long.isFastClickId(
    interval: Long = FastClickHelper.MIN_CLICK_DELAY_TIME,
    block: () -> Unit = {}
) {
    if (FastClickHelper.isFastClickId(this, interval)) {
        block()
    }
}

fun Int.isFastClickId(
    interval: Long = FastClickHelper.MIN_CLICK_DELAY_TIME,
    block: () -> Unit = {}
) {
    this.toLong().isFastClickId(interval, block)
}

fun String.isFastClickId(
    interval: Long = FastClickHelper.MIN_CLICK_DELAY_TIME,
    block: () -> Unit = {}
) {
    this.hashCode().toLong().isFastClickId(interval, block)
}

fun Any.isFastClickId(
    interval: Long = FastClickHelper.MIN_CLICK_DELAY_TIME,
    block: () -> Unit = {}
) {
    this.hashCode().toLong().isFastClickId(interval, block)
}
