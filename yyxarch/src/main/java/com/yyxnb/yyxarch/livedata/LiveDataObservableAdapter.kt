package com.yyxnb.yyxarch.livedata

import android.arch.lifecycle.LiveData
import com.yyx.yyxbase.livedata.DeferredLceeLiveData
import com.yyx.yyxbase.livedata.DeferredLiveData
import com.yyxnb.yyxarch.bean.Lcee
import io.reactivex.Observable
import kotlinx.coroutines.Deferred

/**
 * Description: livedata 工厂类
 *
 * @author : yyx
 * @date ：2018/6/13
 */
object LiveDataObservableAdapter {

    fun <T> fromObservable(observable: Observable<T>): LiveData<T> {
        return ObservableLiveData(observable)
    }

    fun <T> fromObservableLcee(observable: Observable<T>): LiveData<Lcee<T>> {
        return ObservableLceeLiveData(observable)
    }

    fun <T> fromDeferredLcee(deferred: Deferred<T>): LiveData<Lcee<T>> {
        return DeferredLceeLiveData(deferred)
    }

    fun <T> fromDeferred(deferred: Deferred<T>): LiveData<T> {
        return DeferredLiveData(deferred)
    }

}
