package com.yyxnb.arch.vm

import androidx.lifecycle.viewModelScope
import com.yyxnb.arch.TestState
import com.yyxnb.yyxarch.base.mvvm.BaseViewModel
import com.yyxnb.yyxarch.common.Resource
import com.yyxnb.yyxarch.http.exception.ApiException
import com.yyxnb.yyxarch.utils.log.LogUtils
import kotlinx.coroutines.launch

class TestViewModel() : BaseViewModel<TestState, TestRepository>(TestState()) {

    fun getTest() = launchUI {

        //        try {

        setState {
            copy(isLoading = true)
        }

        val data = mRepository.getTest2()

        setState {
            copy(data = data, isLoading = false)
        }


        setState {
            copy(isLoading = false)
        }


//        } catch (e: Exception) {
//            LogUtils.w(ApiException.handleException(e).message)
//            setState {
//                copy(isLoading = false)
//            }
//        }

    }


}
