package com.yyxnb.yyxarch.bean

import java.io.Serializable

/**
 * Bus data
 */
data class MsgEvent(
        var id: Int = 0,
        var status: String? = null,
        var msg: Any? = null
) : Serializable