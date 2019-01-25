package com.yyxnb.yyxarch.http

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.tencent.mmkv.MMKV
import com.yyxnb.yyxarch.http.client.GlobalRxHttp
import com.yyxnb.yyxarch.http.constant.SPKeys
import com.yyxnb.yyxarch.http.download.DownloadRetrofit
import com.yyxnb.yyxarch.http.upload.UploadRetrofit
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import java.util.*

/**
 * 网络请求
 */

class RxHttpUtils {


    /**
     * 必须在全局Application先调用，获取context上下文，否则缓存无法使用
     *
     * @param app Application
     */
    fun init(app: Application): RxHttpUtils {
        context = app
        return this
    }


    fun config(): GlobalRxHttp {
        checkInitialize()
        return GlobalRxHttp.getInstance()
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: RxHttpUtils? = null
        @SuppressLint("StaticFieldLeak")
        private var context: Application? = null


        private var mCompositeDisposable: CompositeDisposable? = null

        private val networkData: String? = null

        fun getInstance(): RxHttpUtils {
            if (instance == null) {
                synchronized(RxHttpUtils::class.java) {
                    if (instance == null) {
                        instance = RxHttpUtils()
                        mCompositeDisposable = CompositeDisposable()
                    }
                }

            }
            return instance!!
        }

        /**
         * 获取全局上下文
         */
        fun getContext(): Context? {
            checkInitialize()
            return context
        }

        /**
         * 检测是否调用初始化方法
         */
        private fun checkInitialize() {
            if (context == null) {
                throw ExceptionInInitializerError("请先在全局Application中调用 RxHttpUtils.init() 初始化！")
            }
        }


        /**
         * 使用全局参数创建请求
         *
         * @param cls Class
         * @param <K> K
         * @return 返回
        </K> */
        fun <K> createApi(cls: Class<K>): K {
            return GlobalRxHttp.createGApi(cls)
        }


        /**
         * 下载文件
         *
         * @param fileUrl
         * @return
         */
        fun downloadFile(fileUrl: String): Observable<ResponseBody> {
            return DownloadRetrofit.downloadFile(fileUrl)
        }

        /**
         * 上传单张图片
         *
         * @param uploadUrl 地址
         * @param filePath  文件路径
         * @return ResponseBody
         */
        fun uploadImg(uploadUrl: String, filePath: String): Observable<ResponseBody> {
            return UploadRetrofit.uploadImg(uploadUrl, filePath)
        }

        /**
         * 上传多张图片
         *
         * @param uploadUrl 地址
         * @param filePaths 文件路径
         * @return ResponseBody
         */
        fun uploadImgs(uploadUrl: String, filePaths: List<String>): Observable<ResponseBody> {
            return UploadRetrofit.uploadImgs(uploadUrl, filePaths)
        }

        /**
         * 获取Cookie
         *
         * @return HashSet
         */
        val cookie: HashSet<String>
            get() = MMKV.defaultMMKV().decodeStringSet(SPKeys.COOKIE, HashSet<String>()) as HashSet<String>

        /**
         * 获取disposable 在onDestroy方法中取消订阅disposable.dispose()
         *
         * @param disposable disposable
         */
        fun addDisposable(disposable: Disposable?) {
            if (mCompositeDisposable != null) {
                mCompositeDisposable!!.add(disposable!!)
            }
        }

        /**
         * 取消所有请求 订阅
         */
        fun cancelAllRequest() {
            if (mCompositeDisposable != null) {
                mCompositeDisposable!!.clear()
            }
        }

        /**
         * 取消单个请求 订阅
         */
        fun cancelSingleRequest(disposable: Disposable?) {
            if (disposable != null && !disposable.isDisposed) {
                disposable.dispose()
            }
        }
    }
}
