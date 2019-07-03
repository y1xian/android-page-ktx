package com.yyxnb.arch

import com.yyxnb.yyxarch.base.mvvm.BaseState

data class TestState(val data: BaseDatas<List<TestData>>? = null,
                     val isLoading: Boolean = false
) : BaseState