package com.yyxnb.arch;

import com.yyxnb.yyxarch.AppUtils;
import com.yyxnb.yyxarch.base.BaseApplication;
import com.yyxnb.yyxarch.http.RxHttpUtils;

public class App extends BaseApplication {

    private final String BASE_URL = "https://api.apiopen.top/";

    @Override
    public void onCreate() {
        super.onCreate();
        initRxHttp();
        AppUtils.Companion.init(this);
    }

    private void initRxHttp() {
        RxHttpUtils.Companion
                .getInstance()
                .init(this)
                //开启全局配置
                .config()
                //全局的BaseUrl
                .setBaseUrl(BASE_URL)
                .setOkClient(getOkHttpClient());
    }
}
