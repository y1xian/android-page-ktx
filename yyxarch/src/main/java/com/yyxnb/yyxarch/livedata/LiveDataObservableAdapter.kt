package com.yyxnb.yyxarch.livedata

import android.arch.lifecycle.LiveData
import com.yyxnb.yyxarch.bean.Lcee
import com.yyxnb.yyxarch.common.AppConfig
import com.yyxnb.yyxarch.utils.RxTransformerUtil
import io.reactivex.Observable
import kotlinx.coroutines.Deferred

/**
 * Description: livedata 工厂类
 *
 * @author : yyx
 * @date ：2018/6/13
 */
object LiveDataObservableAdapter {

    @JvmOverloads
    fun <T> fromObservable(observable: Observable<T>, mRetryMaxTime: Int = AppConfig.retryMaxTime, mRetryDelay: Long = AppConfig.retryDelay): LiveData<T> {
        return ObservableLiveData(observable.compose(RxTransformerUtil.switchSchedulers(mRetryMaxTime, mRetryDelay)))
    }

    @JvmOverloads
    fun <T> fromObservableLcee(observable: Observable<T>, mRetryMaxTime: Int = AppConfig.retryMaxTime, mRetryDelay: Long = AppConfig.retryDelay): LiveData<Lcee<T>> {
        return ObservableLceeLiveData(observable.compose(RxTransformerUtil.switchSchedulers(mRetryMaxTime, mRetryDelay)))
    }

    fun <T> fromDeferredLcee(deferred: Deferred<T>): LiveData<Lcee<T>> {
        return DeferredLceeLiveData(deferred)
    }

    fun <T> fromDeferred(deferred: Deferred<T>): LiveData<T> {
        return DeferredLiveData(deferred)
    }

}
