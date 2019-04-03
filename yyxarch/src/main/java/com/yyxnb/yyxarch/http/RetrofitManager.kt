package com.yyxnb.yyxarch.http

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tencent.mmkv.MMKV
import com.yyxnb.yyxarch.http.config.OkHttpConfig
import com.yyxnb.yyxarch.http.download.DownloadRetrofit
import com.yyxnb.yyxarch.http.gson.GsonAdapter
import com.yyxnb.yyxarch.http.upload.UploadRetrofit
import com.yyxnb.yyxarch.interfaces.ISPKeys
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * Retrofit封装
 */
object RetrofitManager {

    private var mClient: OkHttpClient? = null

    private val mMultiUrl = RetrofitMultiUrl

    /**
     * Service 缓存-避免重复创建同一个Service
     */
    private var mServiceMap = HashMap<String, Any?>()

    private var mCacheEnable: Boolean = true

    private val mCompositeDisposable = CompositeDisposable()

    private val mRetrofitBuilder = Retrofit.Builder().apply {
        addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        addCallAdapterFactory(CoroutineCallAdapterFactory())
        addConverterFactory(GsonConverterFactory.create(GsonAdapter.buildGson()))
    }

    private val mRetrofit: Retrofit
        get() = mRetrofitBuilder.client(mClient ?: OkHttpConfig.okHttpClient).build()

    /**
     * 创建Service
     */
    fun <T> createApi(apiService: Class<T>): T {
        if (mCacheEnable && apiService != null) {
            if (mServiceMap.containsKey(apiService.name)) {
                Log.v("RetrofitManager", "className:" + apiService.name + ";service取自缓存")
                return mServiceMap[apiService.name] as T
            }
            val tClass = mRetrofit.create(apiService)
            mServiceMap[apiService.name] = tClass
            return tClass
        }
        return mRetrofit.create(apiService)
    }

    /**
     * 下载文件
     * @param fileUrl
     */
    fun downloadFile(fileUrl: String): Observable<ResponseBody> {
        return DownloadRetrofit.downloadFile(fileUrl)
    }

    /**
     * 上传单张图片
     * @param uploadUrl 地址
     * @param filePath  文件路径
     */
    fun uploadImg(uploadUrl: String, filePath: String, fileName: String): Observable<ResponseBody> {
        return UploadRetrofit.uploadImg(uploadUrl, filePath, fileName)
    }

    /**
     * 上传多张图片
     * @param uploadUrl 地址
     * @param filePaths 文件路径
     */
    fun uploadImgs(uploadUrl: String, filePaths: List<String>, fileName: String): Observable<ResponseBody> {
        return UploadRetrofit.uploadImgs(uploadUrl, filePaths, fileName)
    }

    /**
     * 获取Cookie
     */
    val cookie: HashSet<String>
        get() = MMKV.defaultMMKV().decodeStringSet(ISPKeys.COOKIE, HashSet<String>()) as HashSet<String>

    /**
     * 设置全局BaseUrl
     */
    fun setBaseUrl(baseUrl: String): RetrofitManager {
        mRetrofitBuilder.baseUrl(baseUrl)
        mMultiUrl.setGlobalBaseUrl(baseUrl)
        return this
    }

    /**
     * 设置自己的client
     */
    fun setOkClient(okClient: OkHttpClient): RetrofitManager {
        mClient = okClient
        return this
    }

    /**
     * Service 缓存
     */
    fun setCacheEnable(userCacheEnable: Boolean): RetrofitManager {
        mCacheEnable = userCacheEnable
        return this
    }

    /**
     * 控制管理器是否拦截,在每个域名地址都已经确定,不需要再动态更改时可设置为 false
     */
    fun setUrlInterceptEnable(enable: Boolean): RetrofitManager {
        mMultiUrl.setIntercept(enable)
        return this
    }

    /**
     * 是否Service Header设置多BaseUrl优先--默认method优先
     */
    fun setHeaderPriorityEnable(enable: Boolean): RetrofitManager {
        mMultiUrl.setHeaderPriorityEnable(enable)
        return this
    }

    /**
     * 存放多BaseUrl 映射关系service 设置header模式-需要才设置
     */
    fun putHeaderBaseUrl(map: Map<String, String>): RetrofitManager {
        mMultiUrl.putHeaderBaseUrl(map)
        return this
    }

    /**
     * 存放多BaseUrl 映射关系设置header模式-需要才设置
     */
    fun putHeaderBaseUrl(urlKey: String, urlValue: String): RetrofitManager {
        mMultiUrl.putHeaderBaseUrl(urlKey, urlValue)
        return this
    }

    /**
     * 存放多BaseUrl 映射关系method模式-需要才设置
     */
    fun putBaseUrl(map: Map<String, String>): RetrofitManager {
        mMultiUrl.putBaseUrl(map)
        return this
    }

    /**
     * 存放多BaseUrl 映射关系method模式-需要才设置
     */
    fun putBaseUrl(method: String, urlValue: String): RetrofitManager {
        mMultiUrl.putBaseUrl(method, urlValue)
        return this
    }

    /**
     * 获取disposable 在onDestroy方法中取消订阅disposable.dispose()
     */
    fun addDisposable(disposable: Disposable?) {
        mCompositeDisposable.add(disposable!!)
    }

    /**
     * 取消所有请求 订阅
     */
    fun cancelAllRequest() {
        mCompositeDisposable.clear()
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
