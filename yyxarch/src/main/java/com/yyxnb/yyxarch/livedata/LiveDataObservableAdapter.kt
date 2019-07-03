package com.yyxnb.yyxarch.livedata

import androidx.lifecycle.LiveData
import com.yyxnb.yyxarch.bean.Lcee
import kotlinx.coroutines.Deferred

/**
 * Description: livedata 工厂类
 *
 * @author : yyx
 * @date ：2018/6/13
 */
object LiveDataObservableAdapter {

    fun <T> fromDeferredLcee(deferred: Deferred<T>): LiveData<Lcee<T>> {
        return DeferredLceeLiveData(deferred)
    }

    fun <T> fromDeferred(deferred: Deferred<T>): LiveData<T> {
        return DeferredLiveData(deferred)
    }

}
