package com.yyxnb.yyxarch.http.observer


import com.yyxnb.yyxarch.http.RetrofitManager
import io.reactivex.disposables.Disposable

/**
 * 通用的Observer
 * 用户可以根据自己需求自定义自己的类继承BaseObserver<T>即可
 *
 * RetrofitManager
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
        RetrofitManager.addDisposable(disposable)
    }

    override fun doOnError(errorMsg: String) {
        onError(errorMsg)
        RetrofitManager.cancelSingleRequest(disposable)
    }

    override fun doOnNext(t: T) {
        onSuccess(t)
    }

    override fun doOnCompleted() {
        RetrofitManager.cancelSingleRequest(disposable)
    }

}
