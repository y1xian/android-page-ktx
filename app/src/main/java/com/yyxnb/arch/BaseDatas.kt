package com.yyxnb.arch

import java.io.Serializable

/**
 * Description:
 *
 * @author : yyx
 * @date : 2018/11/21
 */
class BaseDatas<T> : Serializable {

    var code: Int = 0
    var message: String? = null
    var result: T? = null
}
