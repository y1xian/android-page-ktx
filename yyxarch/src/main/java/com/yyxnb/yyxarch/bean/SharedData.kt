package com.yyxnb.yyxarch.bean

data class SharedData(val msg: String = "",
                      val strRes: Int = 0,
                      val type: SharedType = SharedType.ERROR)