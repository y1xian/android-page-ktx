package com.yyxnb.yyxarch.livedata

import android.arch.lifecycle.LiveData
import com.yyxnb.yyxarch.bean.Lcee
import com.yyxnb.yyxarch.ext.tryCatch
import com.yyxnb.yyxarch.http.exception.ApiException
import com.yyxnb.yyxarch.utils.log.LogUtils
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis


/**
 * @author yyx
 * @date 2017/10/21
 */

internal class DeferredLceeLiveData<T>(private val deferred: Deferred<T>) : LiveData<Lcee<T>>() {

    private val presenterScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + Job())
    }

    override fun onActive() {
        super.onActive()

        presenterScope.launch {
            val time = measureTimeMillis {

                tryCatch({
                    postValue(Lcee.loading())

                    val value = deferred.await()

                    if (value == null) {
                        postValue(Lcee.empty())
                    } else {
                        postValue(Lcee.content(value))
                    }
                }, {
                    LogUtils.e(ApiException.handleException(it).message)
                    postValue(Lcee.error(ApiException.handleException(it).message))
                })

            }
            println("Completed in $time ms")

        }

    }

    override fun onInactive() {
        super.onInactive()
        presenterScope.cancel()
    }
}
