package com.yyxnb.yyxarch.livedata

import android.arch.lifecycle.LiveData
import android.support.annotation.NonNull
import com.yyxnb.yyxarch.bean.Lcee
import com.yyxnb.yyxarch.http.RetrofitManager
import com.yyxnb.yyxarch.http.exception.ApiException
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

internal class ObservableLceeLiveData<T>(private val mObservable: Observable<T>) : LiveData<Lcee<T>>() {
    private var mDisposableRef: WeakReference<Disposable>? = null
    private val mLock = Any()

    override fun onActive() {
        super.onActive()

        mObservable.subscribe(object : Observer<T> {
            override fun onSubscribe(@NonNull d: Disposable) {
                // Don't worry about backpressure. If the stream is too noisy then
                // backpressure can be handled upstream.
                synchronized(mLock) {
                    mDisposableRef = WeakReference(d)
                    RetrofitManager.addDisposable(mDisposableRef!!.get())
                }
                postValue(Lcee.loading())
            }

            override fun onNext(@NonNull t: T) {
                if (null == t) {
                    postValue(Lcee.empty())
                } else {
                    postValue(Lcee.content(t))
                }
            }

            override fun onError(@NonNull t: Throwable) {
                synchronized(mLock) {
                    RetrofitManager.cancelSingleRequest(mDisposableRef!!.get())
                    mDisposableRef = null
                }
                // Errors should be handled upstream, so propagate as a crash.
                postValue(Lcee.error(ApiException.handleException(t).message!!))
            }

            override fun onComplete() {
                synchronized(mLock) {
                    RetrofitManager.cancelSingleRequest(mDisposableRef!!.get())
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
                RetrofitManager.cancelSingleRequest(mDisposableRef!!.get())
                mDisposableRef = null
            }
        }
    }
}
