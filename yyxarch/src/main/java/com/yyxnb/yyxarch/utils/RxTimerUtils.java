package com.yyxnb.yyxarch.utils;

import android.support.annotation.NonNull;

import com.yyxnb.yyxarch.utils.log.LogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 时间操作
 */
public class RxTimerUtils {

    private static Disposable mDisposable;

    /**
     * milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param next
     */
    public static void timer(long milliseconds, final IRxNext next) {
        Observable.timer(milliseconds, TimeUnit.MILLISECONDS)
                .compose(RxTransformerUtil.schedulersTransformer())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long number) {
                        if (next != null) {
                            next.doNext(number);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //取消订阅
                        cancel();
                    }

                    @Override
                    public void onComplete() {
                        //取消订阅
                        cancel();
                    }
                });
    }


    /**
     * 每隔milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param next
     */
    public static void interval(long milliseconds, final IRxNext next) {
        Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
                .compose(RxTransformerUtil.schedulersTransformer())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long number) {
                        if (next != null) {
                            next.doNext(number);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 每隔milliseconds毫秒后执行next操作
     * @param milliseconds 毫秒
     * @param count 次数
     * @param next
     */
    public static void interval(long milliseconds, int count, final IRxNext next) {
        Observable.interval(0, milliseconds, TimeUnit.MILLISECONDS)
                .take(count)
                .compose(RxTransformerUtil.schedulersTransformer())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long number) {
                        if (next != null) {
                            next.doNext(number);
                            if (count == number - 1 ){
                                cancel();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 每隔milliseconds毫秒后执行next操作
     * @param milliseconds 毫秒
     * @param count 次数
     * @param next
     */
    public static void intervalRange (long milliseconds, int count, final IRxNext next) {
        Observable.intervalRange (0,count,0, milliseconds, TimeUnit.MILLISECONDS)
                .compose(RxTransformerUtil.schedulersTransformer())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long number) {
                        if (next != null) {
                            next.doNext(number);
                            if (count == number - 1 ){
                                cancel();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 取消订阅
     */
    public static void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            LogUtils.e("====定时器取消======");
        }
    }

    public interface IRxNext {
        void doNext(long number);
    }
}