package com.yyxnb.yyxarch.http.client

import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * 网络请求工具类---使用的是全局配置的变量
 */

class GlobalRxHttp {

    /**
     * 全局的 RetrofitBuilder
     *
     * @return
     */
    val globalRetrofitBuilder: Retrofit.Builder
        get() = RetrofitClient.getInstance().retrofitBuilder

    /**
     * 设置baseUrl
     *
     * @param baseUrl
     * @return
     */
    fun setBaseUrl(baseUrl: String): GlobalRxHttp {
        globalRetrofitBuilder.baseUrl(baseUrl)
        return this
    }


    /**
     * 设置自己的client
     *
     * @param okClient
     * @return
     */
    fun setOkClient(okClient: OkHttpClient): GlobalRxHttp {
        globalRetrofitBuilder.client(okClient)
        return this
    }

    companion object {

        private var instance: GlobalRxHttp? = null

        fun getInstance(): GlobalRxHttp {

            if (instance == null) {
                synchronized(GlobalRxHttp::class.java) {
                    if (instance == null) {
                        instance = GlobalRxHttp()
                    }
                }

            }
            return instance!!
        }


        /**
         * 全局的 retrofit
         *
         * @return
         */
        val globalRetrofit: Retrofit
            get() = RetrofitClient.getInstance().retrofit

        /**
         * 使用全局变量的请求
         *
         * @param cls
         * @param <K>
         * @return
        </K> */
        fun <K> createGApi(cls: Class<K>): K {
            return globalRetrofit.create(cls)
        }
    }


}
