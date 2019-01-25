package com.yyxnb.yyxarch.livedata

import android.arch.lifecycle.LiveData
import android.support.annotation.NonNull
import com.yyxnb.yyxarch.http.RxHttpUtils
import com.yyxnb.yyxarch.http.exception.ApiException
import com.yyxnb.yyxarch.utils.log.LogUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

internal class ObservableLiveData<T>(private var mObservable: Observable<T>) : LiveData<T>() {
    private var mDisposableRef: WeakReference<Disposable>? = null
    private val mLock = Any()


    override fun onActive() {
        super.onActive()

        mObservable.subscribe(object : Observer<T> {
            override fun onSubscribe(@io.reactivex.annotations.NonNull d: Disposable) {
                // Don't worry about backpressure. If the stream is too noisy then
                // backpressure can be handled upstream.
                synchronized(mLock) {
                    mDisposableRef = WeakReference(d)
                    RxHttpUtils.addDisposable(mDisposableRef!!.get())
                }
            }

            override fun onNext(@NonNull t: T) {
                postValue(t)
            }

            override fun onError(@NonNull t: Throwable) {
                synchronized(mLock) {
                    RxHttpUtils.cancelSingleRequest(mDisposableRef!!.get())
                    mDisposableRef = null
                }
                // Errors should be handled upstream, so propagate as a crash.
                LogUtils.e(ApiException.handleException(t).message)
            }

            override fun onComplete() {
                synchronized(mLock) {
                    RxHttpUtils.cancelSingleRequest(mDisposableRef!!.get())
                    mDisposableRef = null
                }
            }
        })

    }

    override fun onInactive() {
        super.onInactive()
        synchronized(mLock) {
            val subscriptionRef = mDisposableRef
            if (subscriptionRef != null) {
                val subscription = subscriptionRef.get()
                subscription?.dispose()
                RxHttpUtils.cancelSingleRequest(mDisposableRef!!.get())
                mDisposableRef = null
            }
        }
    }
}
