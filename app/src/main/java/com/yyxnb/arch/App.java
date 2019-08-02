package com.yyxnb.arch;

import com.squareup.leakcanary.LeakCanary;
import com.yyxnb.yyxarch.AppUtils;
import com.yyxnb.yyxarch.base.BaseApplication;
import com.yyxnb.yyxarch.http.RetrofitManager;
import com.yyxnb.yyxarch.http.config.OkHttpConfig;
import com.yyxnb.yyxarch.utils.log.LogUtils;

import okhttp3.OkHttpClient;

public class App extends BaseApplication {

    private final String BASE_URL = "https://api.apiopen.top/";
    private final String BASE_URL2 = "https://api.github.com/";
    private final String BASE_URL3 = "http://www.mocky.io/";

    @Override
    public void onCreate() {
        super.onCreate();
        initRxHttp();
        AppUtils.INSTANCE.init(this);

        LogUtils.INSTANCE.init()
                .setTag("Test")//设置全局tag
                .setShowThreadInfo(false).setDebug(true); //是否显示日志，默认true，发布时最好关闭

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);


    }

    private void initRxHttp() {
//        RetrofitManager.INSTANCE.setBaseUrl(BASE_URL);

        RetrofitManager.INSTANCE
                .setBaseUrl(BASE_URL)
                .putBaseUrl(ApiConstant.API_TEST_KEY, BASE_URL3)
                .setOkClient(mClient);


    }

    private OkHttpClient mClient = new OkHttpConfig.Builder()
            //全局的请求头信息
//            .setHeaders(new HashMap<>())
            //开启缓存策略(默认false)
            //1、在有网络的时候，先去读缓存，缓存时间到了，再去访问网络获取数据；
            //2、在没有网络的时候，去读缓存中的数据。
            .setCache(true)
            //全局持久话cookie,保存本地每次都会携带在header中（默认false）
            .setSaveCookie(true)
            //可以添加自己的拦截器(比如使用自己熟悉三方的缓存库等等)
//            .setAddInterceptor(new RetryInterceptor.Builder()
//                    .executionCount(1).retryInterval(888)
//                    .build())
            //全局ssl证书认证
            //1、信任所有证书,不安全有风险（默认信任所有证书）
            .setSslSocketFactory()
            //2、使用预埋证书，校验服务端证书（自签名证书）
//            .setSslSocketFactory(cerInputStream)
            //3、使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
            //.setSslSocketFactory(bksInputStream,"123456",cerInputStream)
            //全局超时配置
            .setTimeout(8)
            //全局是否打开请求log日志
            .setLogEnable(true)
            .build();
}
