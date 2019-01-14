package com.yyxnb.yyxarch.http.interceptor;


import com.yyxnb.yyxarch.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Description: 网络缓存
 *
 * @author : yyx
 * @date ：2018/6/16
 */
public class NetCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        if (NetworkUtils.isAvailable() || NetworkUtils.isConnected()) {
            //如果有网络，缓存60s
            Response response = chain.proceed(request);
            int maxTime = 60;
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + maxTime)
                    .build();
        }
        //如果没有网络，不做处理，直接返回
        return chain.proceed(request);
    }

}