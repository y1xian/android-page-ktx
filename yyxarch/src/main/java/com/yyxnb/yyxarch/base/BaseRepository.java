package com.yyxnb.yyxarch.base;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.yyxnb.yyxarch.AppUtils;
import com.yyxnb.yyxarch.http.RxHttpUtils;


/**
 *  网络请求
 * @author yyx
 */
public abstract class BaseRepository<T> implements DefaultLifecycleObserver {

    protected T mApiServer;

    public BaseRepository() {
        mApiServer = initApiServer(AppUtils.getInstance(this, 0));
    }

    private T initApiServer(@NonNull Class<T> modelClass) {
        return RxHttpUtils.createApi(modelClass);
    }
}
