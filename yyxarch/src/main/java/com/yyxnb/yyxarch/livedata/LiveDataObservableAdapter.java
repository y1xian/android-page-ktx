package com.yyxnb.yyxarch.livedata;

import android.arch.lifecycle.LiveData;

import com.yyxnb.yyxarch.bean.Lcee;
import com.yyxnb.yyxarch.utils.RxTransformerUtil;

import io.reactivex.Observable;

/**
 * Description: livedata 工厂类
 *
 * @author : yyx
 * @date ：2018/6/13
 */
public class LiveDataObservableAdapter {

    public static <T> LiveData<T> fromObservable(final Observable<T> observable) {
        return new ObservableLiveData<>(observable.compose(RxTransformerUtil.schedulersTransformer()));
    }

    public static <T> LiveData<Lcee<T>> fromObservableLcee(final Observable<T> observable) {
        return new ObservableLceeLiveData<>(observable.compose(RxTransformerUtil.schedulersTransformer()));
    }

}
