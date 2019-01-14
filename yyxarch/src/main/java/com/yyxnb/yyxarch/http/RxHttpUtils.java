package com.yyxnb.yyxarch.http;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.yyxnb.yyxarch.http.client.GlobalRxHttp;
import com.yyxnb.yyxarch.http.constant.SPKeys;
import com.yyxnb.yyxarch.http.download.DownloadRetrofit;
import com.yyxnb.yyxarch.http.upload.UploadRetrofit;
import com.yyxnb.yyxarch.utils.PreferencesUtils;

import java.util.HashSet;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 *         网络请求
 */

public class RxHttpUtils {

    @SuppressLint("StaticFieldLeak")
    private static RxHttpUtils instance;
    @SuppressLint("StaticFieldLeak")
    private static Application context;


    private static CompositeDisposable mCompositeDisposable;

    private static String networkData;

        public static RxHttpUtils getInstance() {
        if (instance == null) {
            synchronized (RxHttpUtils.class) {
                if (instance == null) {
                    instance = new RxHttpUtils();
                    mCompositeDisposable = new CompositeDisposable();
                }
            }

        }
        return instance;
    }


    /**
     * 必须在全局Application先调用，获取context上下文，否则缓存无法使用
     *
     * @param app Application
     */
    public RxHttpUtils init(Application app) {
        context = app;
        return this;
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        checkInitialize();
        return context;
    }

    /**
     * 检测是否调用初始化方法
     */
    private static void checkInitialize() {
        if (context == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 RxHttpUtils.init() 初始化！");
        }
    }


    public GlobalRxHttp config() {
        checkInitialize();
        return GlobalRxHttp.getInstance();
    }


    /**
     * 使用全局参数创建请求
     *
     * @param cls Class
     * @param <K> K
     * @return 返回
     */
    public static <K> K createApi(Class<K> cls) {
        return GlobalRxHttp.createGApi(cls);
    }


    /**
     * 下载文件
     *
     * @param fileUrl
     * @return
     */
    public static Observable<ResponseBody> downloadFile(String fileUrl) {
        return DownloadRetrofit.downloadFile(fileUrl);
    }

    /**
     * 上传单张图片
     *
     * @param uploadUrl 地址
     * @param filePath  文件路径
     * @return ResponseBody
     */
    public static Observable<ResponseBody> uploadImg(String uploadUrl, String filePath) {
        return UploadRetrofit.uploadImg(uploadUrl, filePath);
    }

    /**
     * 上传多张图片
     *
     * @param uploadUrl 地址
     * @param filePaths 文件路径
     * @return ResponseBody
     */
    public static Observable<ResponseBody> uploadImgs(String uploadUrl, List<String> filePaths) {
        return UploadRetrofit.uploadImgs(uploadUrl, filePaths);
    }

    /**
     * 获取Cookie
     *
     * @return HashSet
     */
    public static HashSet<String> getCookie() {
        HashSet<String> preferences = (HashSet<String>) PreferencesUtils.get(SPKeys.COOKIE, new HashSet<String>());
        return preferences;
    }

    /**
     * 获取disposable 在onDestroy方法中取消订阅disposable.dispose()
     *
     * @param disposable disposable
     */
    public static void addDisposable(Disposable disposable) {
        if (mCompositeDisposable != null){
            mCompositeDisposable.add(disposable);
        }
    }

    /**
     * 取消所有请求 订阅
     */
    public static void cancelAllRequest() {
        if (mCompositeDisposable != null){
            mCompositeDisposable.clear();
        }
    }

    /**
     * 取消单个请求 订阅
     */
    public static void cancelSingleRequest(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
