package com.yyxnb.yyxarch.http.observer;


import android.text.TextUtils;

import com.yyxnb.yyxarch.AppUtils;
import com.yyxnb.yyxarch.http.RxHttpUtils;

import io.reactivex.disposables.Disposable;

/**
 *         通用的Observer
 *         用户可以根据自己需求自定义自己的类继承BaseObserver<T>即可
 *
 *         RxHttpUtils
 *                  .createApi(ApiService.class)
 *                  .getTestData()
 *                  .compose(RxTransformerUtil.<TestBean>switchSchedulers())
 *                  .subscribe(new CommonObserver<TestBean>() {
 *
 *                      @Override
 *                      protected void onError(String errorMsg) {
 *                           //错误处理
 *                      }
 *
 *                      @Override
 *                      protected void onSuccess(TestBean bookBean) {
 *                           //业务处理
 *                      }
 *                   });
 */

public abstract class CommonObserver<T> extends BaseObserver<T> {

    protected Disposable disposable;

    public CommonObserver() {
    }

    /**
     * 失败回调
     *
     * @param errorMsg
     */
    protected abstract void onError(String errorMsg);

    /**
     * 成功回调
     *
     * @param t
     */
    protected abstract void onSuccess(T t);



    @Override
    public void doOnSubscribe(Disposable d) {
        disposable = d;
        RxHttpUtils.addDisposable(disposable);
    }

    @Override
    public void doOnError(String errorMsg) {
        if (!isHideToast()&& !TextUtils.isEmpty(errorMsg)) {
            AppUtils.debugToast(errorMsg);
        }
        onError(errorMsg);
        RxHttpUtils.cancelSingleRequest(disposable);
    }

    @Override
    public void doOnNext(T t) {
        onSuccess(t);
    }

    @Override
    public void doOnCompleted() {
        RxHttpUtils.cancelSingleRequest(disposable);
    }

}
