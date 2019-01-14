package com.yyxnb.yyxarch.http.download;


import com.yyxnb.yyxarch.http.client.RetrofitClient;
import com.yyxnb.yyxarch.utils.RxTransformerUtil;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *         为下载单独建一个retrofit
 */

public class DownloadRetrofit {

    private static DownloadRetrofit instance;
    private Retrofit mRetrofit;

    private static String baseUrl = "https://api.github.com/";


    public DownloadRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    public static DownloadRetrofit getInstance() {

        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new DownloadRetrofit();
                }
            }

        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public static Observable<ResponseBody> downloadFile(String fileUrl) {
        return DownloadRetrofit
                .getInstance()
                .getRetrofit()
                .create(DownloadApi.class)
                .downloadFile(fileUrl)
                .compose(RxTransformerUtil.<ResponseBody>switchSchedulers());
    }
}
