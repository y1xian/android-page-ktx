package com.yyxnb.arch;

import com.squareup.leakcanary.LeakCanary;
import com.yyxnb.yyxarch.AppUtils;
import com.yyxnb.yyxarch.base.BaseApplication;
import com.yyxnb.yyxarch.http.RetrofitManager;
import com.yyxnb.yyxarch.utils.log.LogUtils;

public class App extends BaseApplication {

    private final String BASE_URL = "https://api.apiopen.top/";
    private final String BASE_URL2 = "https://api.github.com/";

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
                .putBaseUrl(ApiConstant.API_UPDATE_APP, BASE_URL2);


    }
}
