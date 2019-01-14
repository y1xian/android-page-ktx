package com.yyxnb.yyxarch.http.client;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * 网络请求工具类---使用的是全局配置的变量
 */

public class GlobalRxHttp {

    private static GlobalRxHttp instance;

    public GlobalRxHttp() {
    }

    public static GlobalRxHttp getInstance() {

        if (instance == null) {
            synchronized (GlobalRxHttp.class) {
                if (instance == null) {
                    instance = new GlobalRxHttp();
                }
            }

        }
        return instance;
    }

    /**
     * 设置baseUrl
     *
     * @param baseUrl
     * @return
     */
    public GlobalRxHttp setBaseUrl(String baseUrl) {
        getGlobalRetrofitBuilder().baseUrl(baseUrl);
        return this;
    }


    /**
     * 设置自己的client
     *
     * @param okClient
     * @return
     */
    public GlobalRxHttp setOkClient(OkHttpClient okClient) {
        getGlobalRetrofitBuilder().client(okClient);
        return this;
    }


    /**
     * 全局的 retrofit
     *
     * @return
     */
    public static Retrofit getGlobalRetrofit() {
        return RetrofitClient.getInstance().getRetrofit();
    }

    /**
     * 全局的 RetrofitBuilder
     *
     * @return
     */
    public Retrofit.Builder getGlobalRetrofitBuilder() {
        return RetrofitClient.getInstance().getRetrofitBuilder();
    }

    /**
     * 使用全局变量的请求
     *
     * @param cls
     * @param <K>
     * @return
     */
    public static <K> K createGApi(final Class<K> cls) {
        return getGlobalRetrofit().create(cls);
    }


}
