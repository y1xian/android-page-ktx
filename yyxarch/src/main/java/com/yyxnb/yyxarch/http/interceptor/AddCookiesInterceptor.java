package com.yyxnb.yyxarch.http.interceptor;

import android.util.Log;

import com.yyxnb.yyxarch.http.constant.SPKeys;
import com.yyxnb.yyxarch.utils.PreferencesUtils;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *         请求头里边添加cookie
 */

public class AddCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        HashSet<String> preferences = (HashSet<String>) PreferencesUtils.get(SPKeys.COOKIE, new HashSet<String>());
        if (preferences != null) {
            for (String cookie : preferences) {
                builder.addHeader("Cookie", cookie);
                // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
                Log.v("RxHttpUtils", "Adding Header Cookie--->: " + cookie);
            }
        }
        return chain.proceed(builder.build());
    }

}
