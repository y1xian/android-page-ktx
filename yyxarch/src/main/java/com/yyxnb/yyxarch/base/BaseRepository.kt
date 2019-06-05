package com.yyxnb.yyxarch.base

import androidx.lifecycle.DefaultLifecycleObserver
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.http.RetrofitManager


/**
 * 数据仓库
 *
 * 一个数据仓库负责获取同类型的数据来源。比如图书数据仓库能够获取各种条件筛选的图书数据，
 * 这份数据可以来自网络（Retrofit + OKHttp），本地Database（Room），缓存 （HashMap）等等，
 * ViewModel在从Repository获取数据时，不需关注数据具体是怎么来的。
 * @author yyx
 * @date ：2018/6/13
 */
abstract class BaseRepository<T : Any> : DefaultLifecycleObserver {

    protected lateinit var mApi: T

    init {
        mApi = initApiServer(AppUtils.getInstance<Class<T>>(this, 0)!!)
    }

    private fun initApiServer(modelClass: Class<T>): T {
        return RetrofitManager.createApi(modelClass)
    }
}
