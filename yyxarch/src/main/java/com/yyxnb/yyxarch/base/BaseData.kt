package com.yyxnb.yyxarch.base

import java.io.Serializable

/**
 * Description: 通用的数据解析类
 *
 * @author : yyx
 * @date ：2018/6/13
 */
class BaseData<T> : Serializable {

    /**
     * 状态码 一般为 state or code
     */
    var state: Int = 0

    var code: Int = 0
    /**
     * 描述
     */
    var msg: String? = null
    /**
     * 错误 一般没有
     */
    var failData: String? = null
    /**
     * 数据 泛型
     */
    var data: T? = null
}
