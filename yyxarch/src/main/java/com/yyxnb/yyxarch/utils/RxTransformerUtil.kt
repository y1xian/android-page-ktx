package com.yyxnb.yyxarch.utils


import com.yyxnb.yyxarch.common.AppConfig
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * 控制操作线程的辅助类
 */

object RxTransformerUtil {

    /**
     * 无参数
     *
     * @param <T> 泛型
     * @return 返回Observable
    </T> */
    fun <T> switchSchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .doOnSubscribe { disposable ->
                    }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }


    /**
     * 错误重试机制
     */
    fun <T> switchSchedulers(mRetryMaxTime: Int = AppConfig.retryMaxTime, mRetryDelay: Long = AppConfig.retryDelay): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream
                    .retryWhen(RetryWhenUtils(mRetryMaxTime, mRetryDelay))
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .doOnSubscribe { disposable ->
                    }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    /**
     * 线程调度器
     */
    fun <T> schedulersTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream
                    //一般我们在视图消亡后，无需RxJava再执行，可以直接取消订阅
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())

        }
    }

    /**
     * 都在io线程
     *
     * @param <T>
     * @return
    </T> */
    fun <T> allIo(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
        }
    }

}
