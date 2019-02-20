package com.yyx.yyxbase.livedata

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 * @author yyx
 * @date 2017/10/21
 */

internal class DeferredLiveData<T>(private val deferred: Deferred<T>) : LiveData<T>() {

    override fun onActive() {
        super.onActive()

        runBlocking {

            coroutineScope {
                val time = measureTimeMillis {
                    launch {
                        try {
                            postValue(deferred.await())
                        } catch (t: Throwable) {

                        }
                    }
                }
                println("Completed in $time ms")

            }

        }


    }

    override fun onInactive() {
        super.onInactive()

    }

}
