package com.yyxnb.yyxarch.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.yyxnb.yyxarch.AppUtils;
import com.yyxnb.yyxarch.http.config.OkHttpConfig;
import com.yyxnb.yyxarch.http.interceptor.RetryInterceptor;
import com.yyxnb.yyxarch.utils.log.LogUtils;

import java.util.Map;

import okhttp3.OkHttpClient;


/**
 * Description: BaseApplication
 *
 * @author : yyx
 * @date ：2018/6/11
 */
public class BaseApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //突破65535的限制
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.init(this);
        LogUtils.init()
                .setTag("Test")//设置全局tag
                .setShowThreadInfo(true)//是否开启线程信息显示，默认true
                .setDebug(AppUtils.isDebug());//是否显示日志，默认true，发布时最好关闭

    }

    private Map<String, Object> headerMaps;

    private int retry = 1;

    public Map<String, Object> getHeaderMaps() {
        return headerMaps;
    }

    public void setHeaderMaps(Map<String, Object> headerMaps) {
        this.headerMaps = headerMaps;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public OkHttpClient okHttpClient = new OkHttpConfig.Builder()
            //全局的请求头信息
            .setHeaders(headerMaps)
            //开启缓存策略(默认false)
            //1、在有网络的时候，先去读缓存，缓存时间到了，再去访问网络获取数据；
            //2、在没有网络的时候，去读缓存中的数据。
            .setCache(true)
            //全局持久话cookie,保存本地每次都会携带在header中（默认false）
            .setSaveCookie(true)
            //可以添加自己的拦截器(比如使用自己熟悉三方的缓存库等等)
            .setAddInterceptor(new RetryInterceptor.Builder()
                    .executionCount(retry).retryInterval(888)
                    .build())
            //全局ssl证书认证
            //1、信任所有证书,不安全有风险（默认信任所有证书）
            .setSslSocketFactory()
            //2、使用预埋证书，校验服务端证书（自签名证书）
//            .setSslSocketFactory(cerInputStream)
            //3、使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
            //.setSslSocketFactory(bksInputStream,"123456",cerInputStream)
            //全局超时配置
            .setReadTimeout(8)
            //全局超时配置
            .setWriteTimeout(8)
            //全局超时配置
            .setConnectTimeout(8)
            //全局是否打开请求log日志
            .setDebug(true)
            .build();

}
