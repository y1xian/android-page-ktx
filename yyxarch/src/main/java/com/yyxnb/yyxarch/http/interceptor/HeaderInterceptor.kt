package com.yyxnb.yyxarch.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 请求拦截器  统一添加请求头使用
 */

class HeaderInterceptor(private val headerMaps: Map<String, Any>?) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        if (headerMaps != null && headerMaps.size > 0) {
            for ((key, value) in headerMaps) {
                request.addHeader(key, value as String)
            }
        }
        return chain.proceed(request.build())
    }

}
