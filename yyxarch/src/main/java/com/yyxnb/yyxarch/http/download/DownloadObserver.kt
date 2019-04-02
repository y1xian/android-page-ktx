package com.yyxnb.yyxarch.http.download

import android.annotation.SuppressLint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.IOException

/**
 * 文件下载
 *
 * RetrofitManager
 * .downloadFile(url)
 * .subscribe(new DownloadObserver(fileName) {
 * //可以通过配置tag用于取消下载请求
 * @Override
 * protected String setTag() {
 * return "download";
 * }
 *
 * @Override
 * protected void onError(String errorMsg) {
 * }
 *
 * @Override
 * protected void onSuccess(long bytesRead, long contentLength, float progress, boolean done, String filePath) {
 * download_http.setText("下 载中：" + progress + "%");
 * if (done) {
 * responseTv.setText("下载文件路径：" + filePath);
 * }
 *
 * }
 * });
 */

abstract class DownloadObserver constructor(var fileName: String): BaseDownloadObserver() {

    /**
     * 获取disposable 在onDestroy方法中取消订阅disposable.dispose()
     *
     * @param d Disposable
     */
    protected abstract fun getDisposable(d: Disposable)

    /**
     * 失败回调
     *
     * @param errorMsg errorMsg
     */
    protected abstract fun onError(errorMsg: String)

    /**
     * 成功回调
     *
     * @param filePath filePath
     */
    /**
     * 成功回调
     *
     * @param bytesRead     已经下载文件的大小
     * @param contentLength 文件的大小
     * @param progress      当前进度
     * @param done          是否下载完成
     * @param filePath      文件路径
     */
    protected abstract fun onSuccess(bytesRead: Long, contentLength: Long, progress: Float, done: Boolean, filePath: String)


    override fun doOnError(errorMsg: String) {
        onError(errorMsg)
    }

    override fun onSubscribe(@NonNull d: Disposable) {
        getDisposable(d)
    }

    @SuppressLint("CheckResult")
    override fun onNext(@NonNull responseBody: ResponseBody) {
        Observable
                .just(responseBody)
                .subscribeOn(Schedulers.io())
                .subscribe { responseBody ->
                    try {
                        DownloadManager().saveFile(responseBody, fileName, object : IProgressListener {
                            override fun onResponseProgress(bytesRead: Long, contentLength: Long, progress: Int, done: Boolean, filePath: String) {
                                Observable
                                        .just(progress)
                                        .distinctUntilChanged()
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe { onSuccess(bytesRead, contentLength, progress.toFloat(), done, filePath) }
                            }

                        })

                    } catch (e: IOException) {
                        doOnError(e.message!!)
                    }
                }

    }

    override fun onComplete() {

    }
}
