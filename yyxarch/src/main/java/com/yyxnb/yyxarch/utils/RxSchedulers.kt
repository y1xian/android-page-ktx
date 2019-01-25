package com.yyxnb.yyxarch.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object RxSchedulers {
    val database: Scheduler = Schedulers.single()
    val io: Scheduler = Schedulers.io()
    val ui: Scheduler = AndroidSchedulers.mainThread()
}