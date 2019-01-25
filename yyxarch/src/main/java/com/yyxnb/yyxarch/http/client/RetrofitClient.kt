package com.yyxnb.yyxarch.http.client


import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.yyxnb.yyxarch.http.config.OkHttpConfig
import com.yyxnb.yyxarch.http.gson.GsonAdapter
import com.yyxnb.yyxarch.http.interceptor.RxHttpLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient工具类
 */

class RetrofitClient {

    val retrofitBuilder: Retrofit.Builder

    private var okHttpClient: OkHttpClient? = null

    val retrofit: Retrofit
        get() = if (null == OkHttpConfig.okHttpClient) {
            retrofitBuilder.client(okHttpClient!!).build()
        } else {
            retrofitBuilder.client(OkHttpConfig.okHttpClient).build()
        }

    init {

        initDefaultOkHttpClient()


        retrofitBuilder = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(GsonAdapter.buildGson()))
    }

    private fun initDefaultOkHttpClient() {
        val builder = OkHttpClient.Builder()

        builder.readTimeout(8, TimeUnit.SECONDS)
        builder.writeTimeout(8, TimeUnit.SECONDS)
        builder.connectTimeout(8, TimeUnit.SECONDS)

        val sslParams = SSLUtils.sslSocketFactory
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)

        val loggingInterceptor = HttpLoggingInterceptor(RxHttpLogger())
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }

    companion object {

        private var instance: RetrofitClient? = null


        fun getInstance(): RetrofitClient {

            if (instance == null) {
                synchronized(RetrofitClient::class.java) {
                    if (instance == null) {
                        instance = RetrofitClient()
                    }
                }

            }
            return instance!!
        }
    }

}
