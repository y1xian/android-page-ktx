package com.yyxnb.yyxarch.utils


import java.util.*

/**
 * 根据不同后台要求对post请求进行加工处理
 * 用户根据自己需求编写自己的map操作类
 */
class ParamUtils {

    private var params: MutableMap<String, Any>? = null


    fun addParams(key: String, value: Any): ParamUtils {
        if (params == null) {
            params = TreeMap()
        }

        params!![key] = value
        return this
    }

    fun getParams(): Map<*, *>? {
        return if (params == null) {
            null
        } else params

    }

    fun clearParams() {
        if (params != null) {
            params!!.clear()
        }
    }
}
