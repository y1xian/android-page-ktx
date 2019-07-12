package com.yyxnb.yyxarch.http.observer


import com.yyxnb.yyxarch.http.exception.ApiException

import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable


/**
 * 基类BaseObserver
 */

abstract class BaseObserver<T> : Observer<T>, ISubscriber<T> {

    /**
     * 是否隐藏toast
     *
     * @return
     */
    protected val isHideToast: Boolean
        get() = false

    override fun onSubscribe(@NonNull d: Disposable) {
        doOnSubscribe(d)
    }

    override fun onNext(@NonNull t: T) {
        doOnNext(t)
    }

    override fun onError(@NonNull e: Throwable) {
        setError(ApiException.handleException(e).message)
    }


    override fun onComplete() {
        doOnCompleted()
    }


    private fun setError(errorMsg: String) {
        doOnError(errorMsg)
    }

}
