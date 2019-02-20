package com.yyx.yyxbase.livedata

import android.arch.lifecycle.LiveData
import com.yyxnb.yyxarch.bean.Lcee
import com.yyxnb.yyxarch.http.exception.ApiException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis


/**
 * @author yyx
 * @date 2017/10/21
 */

internal class DeferredLceeLiveData<T>(private val deferred: Deferred<T>) : LiveData<Lcee<T>>() {

    override fun onActive() {
        super.onActive()
        runBlocking {
            coroutineScope {

                val time = measureTimeMillis {
                    launch {
                        try {

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
                println("Completed in $time ms")

            }
            postValue(Lcee.loading())
        }

    }

    override fun onInactive() {
        super.onInactive()
    }
}
