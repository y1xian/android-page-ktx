package com.yyxnb.yyxarch.http.download


import com.yyxnb.yyxarch.http.client.RetrofitClient
import com.yyxnb.yyxarch.utils.RxTransformerUtil

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 为下载单独建一个retrofit
 */

class DownloadRetrofit {
    val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
    }

    companion object {

        private var instance: DownloadRetrofit? = null

        private val baseUrl = "https://api.github.com/"

        fun getInstance(): DownloadRetrofit {

            if (instance == null) {
                synchronized(RetrofitClient::class.java) {
                    if (instance == null) {
                        instance = DownloadRetrofit()
                    }
                }

            }
            return instance!!
        }

        fun downloadFile(fileUrl: String): Observable<ResponseBody> {
            return DownloadRetrofit
                    .getInstance()
                    .retrofit
                    .create(DownloadApi::class.java)
                    .downloadFile(fileUrl)
                    .compose(RxTransformerUtil.switchSchedulers())
        }
    }
}
