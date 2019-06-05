package com.yyxnb.yyxarch.livedata

import androidx.lifecycle.LiveData
import com.yyxnb.yyxarch.bean.Lcee
import com.yyxnb.yyxarch.http.exception.ApiException
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis


/**
 * @author yyx
 * @date 2017/10/21
 */

internal class DeferredLceeLiveData<T>(private val deferred: Deferred<T>) : LiveData<Lcee<T>>() {

    lateinit var job: Job

    override fun onActive() {
        super.onActive()

        job = GlobalScope.launch(Dispatchers.Main) {
            val time = measureTimeMillis {

                try {

                    postValue(Lcee.loading())

                    val value = deferred.await()

                    if (value == null) {
                        postValue(Lcee.empty())
                    } else {
                        postValue(Lcee.content(value))
                    }
                } catch (t: Throwable) {
                    postValue(Lcee.error(ApiException.handleException(t).message))
                }

            }
            println("Completed in $time ms")
        }

    }

    override fun onInactive() {
        super.onInactive()
        job.cancel()
    }
}
