package com.yyxnb.yyxarch.http.observer


import android.text.TextUtils

import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.http.RxHttpUtils

import io.reactivex.disposables.Disposable

/**
 * 通用的Observer
 * 用户可以根据自己需求自定义自己的类继承BaseObserver<T>即可
 *
 * RxHttpUtils
 * .createApi(ApiService.class)
 * .getTestData()
 * .compose(RxTransformerUtil.<TestBean>switchSchedulers())
 * .subscribe(new CommonObserver<TestBean>() {
 *
 * @Override
 * protected void onError(String errorMsg) {
 * //错误处理
 * }
 *
 * @Override
 * protected void onSuccess(TestBean bookBean) {
 * //业务处理
 * }
 * });
</TestBean></TestBean></T> */

abstract class CommonObserver<T> : BaseObserver<T>() {

    protected var disposable: Disposable? = null

    /**
     * 失败回调
     *
     * @param errorMsg
     */
    protected abstract fun onError(errorMsg: String)

    /**
     * 成功回调
     *
     * @param t
     */
    protected abstract fun onSuccess(t: T)


    override fun doOnSubscribe(d: Disposable) {
        disposable = d
        RxHttpUtils.addDisposable(disposable)
    }

    override fun doOnError(errorMsg: String) {
        if (!isHideToast && !TextUtils.isEmpty(errorMsg)) {
            AppUtils.debugToast(errorMsg)
        }
        onError(errorMsg)
        RxHttpUtils.cancelSingleRequest(disposable)
    }

    override fun doOnNext(t: T) {
        onSuccess(t)
    }

    override fun doOnCompleted() {
        RxHttpUtils.cancelSingleRequest(disposable)
    }

}
