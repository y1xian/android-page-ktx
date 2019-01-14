package com.yyxnb.yyxarch.livedata;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.yyxnb.yyxarch.bean.Lcee;
import com.yyxnb.yyxarch.http.RxHttpUtils;
import com.yyxnb.yyxarch.http.exception.ApiException;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

class ObservableLceeLiveData<T> extends LiveData<Lcee<T>> {
    private WeakReference<Disposable> mDisposableRef;
    private final Observable<T> mObservable;
    private final Object mLock = new Object();

    ObservableLceeLiveData(@NonNull final Observable<T> observable) {
        mObservable = observable;
    }

    @Override
    protected void onActive() {
        super.onActive();

        mObservable.subscribe(new Observer<T>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                // Don't worry about backpressure. If the stream is too noisy then
                // backpressure can be handled upstream.
                synchronized (mLock) {
                    mDisposableRef = new WeakReference<>(d);
                    RxHttpUtils.addDisposable(mDisposableRef.get());
                }
                postValue(Lcee.<T>loading());
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull T t) {
                if (null == t) {
                    postValue(Lcee.<T>empty());
                } else {
                    postValue(Lcee.content(t));
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable t) {
                synchronized (mLock) {
                    RxHttpUtils.cancelSingleRequest(mDisposableRef.get());
                    mDisposableRef = null;
                }
                // Errors should be handled upstream, so propagate as a crash.
                postValue(Lcee.<T>error(ApiException.handleException(t).getMessage()));
            }

            @Override
            public void onComplete() {
                synchronized (mLock) {
                    RxHttpUtils.cancelSingleRequest(mDisposableRef.get());
                    mDisposableRef = null;
                }
            }
        });

    }

    @Override
    protected void onInactive() {
        super.onInactive();
        synchronized (mLock) {
            WeakReference<Disposable> subscriptionRef = mDisposableRef;
            if (subscriptionRef != null) {
                Disposable subscription = subscriptionRef.get();
                if (subscription != null) {
                    subscription.dispose();
                }
                RxHttpUtils.cancelSingleRequest(mDisposableRef.get());
                mDisposableRef = null;
            }
        }
    }
}
