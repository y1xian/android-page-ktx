package com.yyxnb.yyxarch.http.download


import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.http.exception.ApiException

import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import okhttp3.ResponseBody


abstract class BaseDownloadObserver : Observer<ResponseBody> {

    /**
     * 失败回调
     *
     * @param errorMsg 错误信息
     */
    protected abstract fun doOnError(errorMsg: String)


    override fun onError(@NonNull e: Throwable) {
        val error = ApiException.handleException(e).message
        setError(error!!)
    }

    private fun setError(errorMsg: String) {
        AppUtils.debugToast(errorMsg)
        doOnError(errorMsg)
    }

}
