package com.yyxnb.yyxarch.http.observer

import io.reactivex.disposables.Disposable

/**
 * 定义请求结果处理接口
 */

interface ISubscriber<T> {

    /**
     * doOnSubscribe 回调
     *
     * @param d Disposable
     */
    fun doOnSubscribe(d: Disposable)

    /**
     * 错误回调
     *
     * @param errorMsg 错误信息
     */
    fun doOnError(errorMsg: String)

    /**
     * 成功回调
     *
     * @param t 泛型
     */
    fun doOnNext(t: T)

    /**
     * 请求完成回调
     */
    fun doOnCompleted()
}
