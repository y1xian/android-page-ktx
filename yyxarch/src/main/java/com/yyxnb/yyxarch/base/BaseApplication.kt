package com.yyxnb.yyxarch.base

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.github.anzewei.parallaxbacklayout.ParallaxHelper
import com.jeremyliao.liveeventbus.LiveEventBus
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.http.config.OkHttpConfig
import com.yyxnb.yyxarch.utils.log.LogUtils


/**
 * Description: BaseApplication
 *
 * @author : yyx
 * @date ：2018/6/11
 */
open class BaseApplication : Application() {

    var headerMaps = HashMap<String, Any>()

    var retry = 1

    var okHttpClient = OkHttpConfig.Builder()
            //全局的请求头信息
            .setHeaders(headerMaps)
            //开启缓存策略(默认false)
            //1、在有网络的时候，先去读缓存，缓存时间到了，再去访问网络获取数据；
            //2、在没有网络的时候，去读缓存中的数据。
            .setCache(true)
            //全局持久话cookie,保存本地每次都会携带在header中（默认false）
            .setSaveCookie(true)
            //可以添加自己的拦截器(比如使用自己熟悉三方的缓存库等等)
            //.setAddInterceptor(...)
            //全局ssl证书认证
            //1、信任所有证书,不安全有风险（默认信任所有证书）
            .setSslSocketFactory()
            //2、使用预埋证书，校验服务端证书（自签名证书）
            //.setSslSocketFactory(cerInputStream)
            //3、使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
            //.setSslSocketFactory(bksInputStream,"123456",cerInputStream)
            //全局超时配置
            .setTimeout(8)
            //全局是否打开请求log日志
            .setLogEnable(true)
            .build()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //突破65535的限制
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        AppUtils.init(this)
        LogUtils.init()
                .setTag("Test")//设置全局tag
                .setShowThreadInfo(true).setDebug(AppUtils.isDebug) //是否显示日志，默认true，发布时最好关闭

        registerActivityLifecycleCallbacks(ParallaxHelper.getInstance());

        LiveEventBus.get()
                .config()
                .supportBroadcast(this)
                .lifecycleObserverAlwaysActive(true) //配置支持跨进程、跨APP通信

    }

}
