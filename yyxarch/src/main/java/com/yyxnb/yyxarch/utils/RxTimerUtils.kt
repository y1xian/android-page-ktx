package com.yyxnb.yyxarch.utils

import com.yyxnb.yyxarch.utils.log.LogUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * 时间操作
 */
object RxTimerUtils {

    private var mDisposable: Disposable? = null

    /**
     * milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param next
     */
    fun timer(milliseconds: Long, next: IRxNext?) {
        Observable.timer(milliseconds, TimeUnit.MILLISECONDS)
                .compose(RxTransformerUtil.schedulersTransformer())
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(disposable: Disposable) {
                        mDisposable = disposable
                    }

                    override fun onNext(number: Long) {
                        next?.doNext(number)
                    }

                    override fun onError(e: Throwable) {
                        //取消订阅
                        cancel()
                    }

                    override fun onComplete() {
                        //取消订阅
                        cancel()
                    }
                })
    }


    /**
     * 每隔milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param next
     */
    fun interval(milliseconds: Long, next: IRxNext?) {
        Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
                .compose(RxTransformerUtil.schedulersTransformer())
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(disposable: Disposable) {
                        mDisposable = disposable
                    }

                    override fun onNext(number: Long) {
                        next?.doNext(number)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
    }

    /**
     * 每隔milliseconds毫秒后执行next操作
     * @param milliseconds 毫秒
     * @param count 次数
     * @param next
     */
    fun interval(milliseconds: Long, count: Int, next: IRxNext?) {
        Observable.interval(0, milliseconds, TimeUnit.MILLISECONDS)
                .take(count.toLong())
                .compose(RxTransformerUtil.schedulersTransformer())
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(disposable: Disposable) {
                        mDisposable = disposable
                    }

                    override fun onNext(number: Long) {
                        if (next != null) {
                            next.doNext(number)
                            if (count.toLong() == number - 1) {
                                cancel()
                            }
                        }
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
    }

    /**
     * 每隔milliseconds毫秒后执行next操作
     * @param milliseconds 毫秒
     * @param count 次数
     * @param next
     */
    fun intervalRange(milliseconds: Long, count: Int, next: IRxNext?) {
        Observable.intervalRange(0, count.toLong(), 0, milliseconds, TimeUnit.MILLISECONDS)
                .compose(RxTransformerUtil.schedulersTransformer())
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(disposable: Disposable) {
                        mDisposable = disposable
                    }

                    override fun onNext(number: Long) {
                        if (next != null) {
                            next.doNext(number)
                            if (count.toLong() == number - 1) {
                                cancel()
                            }
                        }
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
    }


    /**
     * 取消订阅
     */
    fun cancel() {
        if (mDisposable != null && !mDisposable!!.isDisposed) {
            mDisposable!!.dispose()
            LogUtils.e("====定时器取消======")
        }
    }

    interface IRxNext {
        fun doNext(number: Long)
    }
}