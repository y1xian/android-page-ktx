package com.yyxnb.yyxarch.livedata;

import android.arch.lifecycle.LiveData;

import org.reactivestreams.Subscriber;

import io.reactivex.Flowable;

public class LiveDataUtils<T>  extends Flowable<T> {


    @Override
    protected void subscribeActual(Subscriber<? super T> s) {

    }
}
