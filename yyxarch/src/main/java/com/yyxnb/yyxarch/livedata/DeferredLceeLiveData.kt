package com.yyx.yyxbase.livedata

import android.arch.lifecycle.LiveData
import com.yyxnb.yyxarch.bean.Lcee
import com.yyxnb.yyxarch.http.exception.ApiException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking


/**
 * @author yyx
 * @date 2017/10/21
 */

internal class DeferredLceeLiveData<T>(private val deferred: Deferred<T>) : LiveData<Lcee<T>>() {

    override fun onActive() {
        super.onActive()

        runBlocking {
            coroutineScope{
                async {
                    try {
                        postValue(Lcee.loading())

                        val value = deferred.await()

                        if (value == null) {
                            postValue(Lcee.empty())
                        } else {
                            postValue(Lcee.content(value))
                        }
                    } catch (t: Throwable) {
                        postValue(Lcee.error(ApiException.handleException(t).message!!))
                    }
                }

            }

        }

    }

    override fun onInactive() {
        super.onInactive()
    }
}
