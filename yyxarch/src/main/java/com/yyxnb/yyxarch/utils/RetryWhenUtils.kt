package com.yyxnb.yyxarch.utils


import android.content.Context
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 *  RxJava 重试机制--retryWhen操作符[Observable.retryWhen]
 */
class RetryWhenUtils @JvmOverloads constructor(private val mContext: Context? = null,
                                               /**
                                                * 最大尝试次数--不包含原始请求次数
                                                */
                                               private var mRetryMaxTime: Int = 3,
                                               /**
                                                * 尝试时间间隔ms
                                                */
                                               private var mRetryDelay: Long = 500) : Function<Observable<out Throwable>, ObservableSource<*>> {
    /**
     * 记录已尝试次数
     */
    private var mRetryCount: Int = 0
    private val TAG = javaClass.simpleName

    /**
     * 重试间隔
     *
     * @param delay
     * @return
     */
    fun setRetryDelay(delay: Long): RetryWhenUtils {
        mRetryDelay = delay
        return this
    }

    /**
     * 重试次数
     *
     * @param time
     * @return
     */
    fun setRetryMaxTime(time: Int): RetryWhenUtils {
        mRetryMaxTime = time
        return this
    }

    /**
     * Applies this function to the given argument.
     *
     * @param observable the function argument
     * @return the function result
     */
    override fun apply(observable: Observable<out Throwable>): Observable<*> {
        return observable.flatMap(Function<Throwable, ObservableSource<*>> { throwable ->
            //未连接网络直接返回异常
            if (!NetworkUtils.isConnected) {
                return@Function Observable.error<Any>(throwable)
            }
            //仅仅对连接失败相关错误进行重试
            if (throwable is ConnectException
                    || throwable is UnknownHostException
                    || throwable is SocketTimeoutException
                    || throwable is SocketException
                    || throwable is TimeoutException) {
                if (++mRetryCount <= mRetryMaxTime) {
                    Log.e(TAG, "网络请求错误,将在 $mRetryDelay ms后进行重试, 重试次数 $mRetryCount;throwable:$throwable")
                    return@Function Observable.timer(mRetryDelay, TimeUnit.MILLISECONDS)
                }
            }
            Observable.error<Any>(throwable)
        })
    }
}
