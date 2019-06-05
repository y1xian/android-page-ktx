package com.yyxnb.yyxarch.livedata

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * @author yyx
 * @date 2017/10/21
 */

internal class DeferredLiveData<T>(private val deferred: Deferred<T>) : LiveData<T>() {

    lateinit var job: Job

    override fun onActive() {
        super.onActive()

        job = GlobalScope.launch(Dispatchers.Main) {
            val time = measureTimeMillis {

                try {

                    postValue(deferred.await())

                } catch (t: Throwable) {

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
