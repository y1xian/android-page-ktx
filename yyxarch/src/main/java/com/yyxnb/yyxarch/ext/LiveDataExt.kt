package com.yyxnb.yyxarch.ext

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.yyxnb.yyxarch.utils.RxSchedulers
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Scheduler
import io.reactivex.android.MainThreadDisposable

fun <T> LiveData<T>.toReactiveStream(observerScheduler: Scheduler = RxSchedulers.ui): Flowable<T> = Flowable
        .create({ emitter: FlowableEmitter<T> ->
            val observer = Observer<T> { data ->
                data?.let {
                    emitter.onNext(it)
                }
            }
            observeForever(observer)

            emitter.setCancellable {
                object : MainThreadDisposable() {
                    override fun onDispose() {
                        removeObserver(observer)
                    }
                }
            }
        }, BackpressureStrategy.LATEST)
        .subscribeOn(RxSchedulers.ui)
        .observeOn(observerScheduler)

fun <X, Y> LiveData<X>.map(function: (X) -> Y): LiveData<Y> =
        Transformations.map(this, function)

fun <X, Y> LiveData<X>.switchMap(function: (X) -> LiveData<Y>): LiveData<Y> =
        Transformations.switchMap(this, function)