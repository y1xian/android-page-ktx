package com.yyxnb.yyxarch.base

import android.arch.lifecycle.DefaultLifecycleObserver

import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.http.RxHttpUtils


/**
 * 网络请求
 * @author yyx
 */
abstract class BaseRepository<T> : DefaultLifecycleObserver {

    protected var mApiServer: T? = null

    init {
        mApiServer = initApiServer(AppUtils.getInstance<Class<T>>(this, 0)!!)
    }

    private fun initApiServer(modelClass: Class<T>): T {
        return RxHttpUtils.createApi(modelClass)
    }
}
