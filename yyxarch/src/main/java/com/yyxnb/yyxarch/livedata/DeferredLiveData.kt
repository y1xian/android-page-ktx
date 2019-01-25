package com.yyx.yyxbase.livedata

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

/**
 * @author yyx
 * @date 2017/10/21
 */

internal class DeferredLiveData<T>(private val deferred: Deferred<T>) : LiveData<T>() {

    override fun onActive() {
        super.onActive()

        runBlocking {
            coroutineScope{
                async {
                    try {
                        postValue(deferred.await())
                    }catch (t:Throwable){

                    }
                }

            }

        }


    }

    override fun onInactive() {
        super.onInactive()

    }

}
