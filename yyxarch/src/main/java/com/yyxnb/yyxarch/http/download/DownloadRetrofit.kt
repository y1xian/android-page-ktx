package com.yyxnb.yyxarch.http.download


import com.yyxnb.yyxarch.utils.RxTransformerUtil
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 为下载单独建一个retrofit
 */

object DownloadRetrofit {

    private const val baseUrl = "https://api.github.com/"

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()


    fun downloadFile(fileUrl: String): Observable<ResponseBody> {
        return DownloadRetrofit
                .retrofit
                .create(IDownloadApi::class.java)
                .downloadFile(fileUrl)
                .compose(RxTransformerUtil.switchSchedulers())
    }
}

