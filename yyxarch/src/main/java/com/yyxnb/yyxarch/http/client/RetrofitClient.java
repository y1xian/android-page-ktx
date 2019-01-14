package com.yyxnb.yyxarch.http.client;


import com.yyxnb.yyxarch.http.config.OkHttpConfig;
import com.yyxnb.yyxarch.http.gson.GsonAdapter;
import com.yyxnb.yyxarch.http.interceptor.RxHttpLogger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient工具类
 */

public class RetrofitClient {

    private static RetrofitClient instance;

    private Retrofit.Builder mRetrofitBuilder;

    private OkHttpClient okHttpClient;

    public RetrofitClient() {

        initDefaultOkHttpClient();


        mRetrofitBuilder = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonAdapter.buildGson()));
    }

    private void initDefaultOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.readTimeout(8, TimeUnit.SECONDS);
        builder.writeTimeout(8, TimeUnit.SECONDS);
        builder.connectTimeout(8, TimeUnit.SECONDS);

        SSLUtils.SSLParams sslParams = SSLUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new RxHttpLogger());
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);

        okHttpClient = builder.build();
    }


    public static RetrofitClient getInstance() {

        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }

        }
        return instance;
    }


    public Retrofit.Builder getRetrofitBuilder() {
        return mRetrofitBuilder;
    }

    public Retrofit getRetrofit() {
        if (null == OkHttpConfig.getOkHttpClient()) {
            return mRetrofitBuilder.client(okHttpClient).build();
        } else {
            return mRetrofitBuilder.client(OkHttpConfig.getOkHttpClient()).build();
        }
    }

}
