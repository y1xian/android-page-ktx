package com.yyxnb.yyxarch.http.config

import android.os.Environment
import android.text.TextUtils
import com.yyxnb.yyxarch.http.client.HttpClient
import com.yyxnb.yyxarch.http.client.SSLUtils
import com.yyxnb.yyxarch.http.interceptor.*
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

class OkHttpConfig {
    init {
        okHttpClientBuilder = OkHttpClient.Builder()
    }

    class Builder {
        private var headerMaps: Map<String, Any>? = null
        private var isDebug: Boolean = false
        private var isCache: Boolean = false
        private var cachePath: String? = null
        private var cacheMaxSize: Long = (1024 * 1024 * 100).toLong()
        private var isSaveCookie: Boolean = false
        private var readTimeout: Long = 10L
        private var writeTimeout: Long = 10L
        private var connectTimeout: Long = 30L
        private var bksFile: InputStream? = null
        private var password: String? = null
        private var certificates: Array<out InputStream>? = null
        private var interceptors: Array<out Interceptor>? = null

        fun setHeaders(headerMaps: Map<String, Any>): Builder {
            this.headerMaps = headerMaps
            return this
        }

        fun setDebug(isDebug: Boolean): Builder {
            this.isDebug = isDebug
            return this
        }

        fun setCache(isCache: Boolean): Builder {
            this.isCache = isCache
            return this
        }

        fun setCachePath(cachePath: String): Builder {
            this.cachePath = cachePath
            return this
        }

        fun setCacheMaxSize(cacheMaxSize: Long): Builder {
            this.cacheMaxSize = cacheMaxSize
            return this
        }

        fun setSaveCookie(isSaveCookie: Boolean): Builder {
            this.isSaveCookie = isSaveCookie
            return this
        }

        fun setReadTimeout(readTimeout: Long): Builder {
            this.readTimeout = readTimeout
            return this
        }

        fun setWriteTimeout(writeTimeout: Long): Builder {
            this.writeTimeout = writeTimeout
            return this
        }

        fun setConnectTimeout(connectTimeout: Long): Builder {
            this.connectTimeout = connectTimeout
            return this
        }

        fun setAddInterceptor(vararg interceptors: Interceptor): Builder {
            this.interceptors = interceptors
            return this
        }

        fun setSslSocketFactory(vararg certificates: InputStream): Builder {
            this.certificates = certificates
            return this
        }

        fun setSslSocketFactory(bksFile: InputStream, password: String, vararg certificates: InputStream): Builder {
            this.bksFile = bksFile
            this.password = password
            this.certificates = certificates
            return this
        }


        fun build(): OkHttpClient {

            OkHttpConfig.getInstance()

            setCookieConfig()
            setCacheConfig()
            setHeadersConfig()
            setSslConfig()
            addInterceptors()
            setTimeout()
            setDebugConfig()

            okHttpClient = okHttpClientBuilder.build()
            return okHttpClient
        }

        private fun addInterceptors() {
            if (null != interceptors) {
                for (interceptor in interceptors!!) {
                    okHttpClientBuilder.addInterceptor(interceptor)
                }
            }
        }

        /**
         * 配置开发环境
         */
        private fun setDebugConfig() {
            if (isDebug) {
                val logInterceptor = HttpLoggingInterceptor(RxHttpLogger())
                logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                okHttpClientBuilder.addInterceptor(logInterceptor)
            }
        }


        /**
         * 配置headers
         */
        private fun setHeadersConfig() {
            okHttpClientBuilder.addInterceptor(HeaderInterceptor(headerMaps))
        }

        /**
         * 配饰cookie保存到sp文件中
         */
        private fun setCookieConfig() {
            if (isSaveCookie) {
                okHttpClientBuilder
                        .addInterceptor(AddCookiesInterceptor())
                        .addInterceptor(ReceivedCookiesInterceptor())
            }

        }

        /**
         * 配置缓存
         */
        private fun setCacheConfig() {
            if (isCache) {
                val cache: Cache
                if (!TextUtils.isEmpty(cachePath) && cacheMaxSize > 0) {
                    cache = Cache(File(cachePath), cacheMaxSize)
                } else {
                    cache = Cache(File(defaultCachePath), defaultCacheSize)
                }
                okHttpClientBuilder
                        .cache(cache)
                        .addInterceptor(NoNetCacheInterceptor())
                        .addNetworkInterceptor(NetCacheInterceptor())
            }
        }

        /**
         * 配置超时信息
         */
        private fun setTimeout() {
            okHttpClientBuilder.readTimeout(readTimeout, TimeUnit.SECONDS)
            okHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS)
            okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS)
            okHttpClientBuilder.retryOnConnectionFailure(true)
        }

        /**
         * 配置证书
         */
        private fun setSslConfig() {
            var sslParams: SSLUtils.SSLParams? = null

            if (null == certificates) {
                //信任所有证书,不安全有风险
                sslParams = SSLUtils.sslSocketFactory
            } else {
                if (null != bksFile && !TextUtils.isEmpty(password)) {
                    //使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
                    sslParams = SSLUtils.getSslSocketFactory(bksFile, password, *certificates!!)
                } else {
                    //使用预埋证书，校验服务端证书（自签名证书）
                    sslParams = SSLUtils.getSslSocketFactory(*certificates!!)
                }
            }

            okHttpClientBuilder.sslSocketFactory(sslParams!!.sSLSocketFactory, sslParams.trustManager)

        }
    }

    companion object {


        private val defaultCachePath = Environment.getExternalStorageDirectory().path + "/rxHttpCacheData"
        private val defaultCacheSize = (1024 * 1024 * 100).toLong()


        private var instance: OkHttpConfig? = null

        private lateinit var okHttpClientBuilder: OkHttpClient.Builder

        lateinit var okHttpClient: OkHttpClient

        fun getInstance(): OkHttpConfig {

            if (instance == null) {
                synchronized(HttpClient::class.java) {
                    if (instance == null) {
                        instance = OkHttpConfig()
                    }
                }

            }
            return instance!!
        }
    }
}