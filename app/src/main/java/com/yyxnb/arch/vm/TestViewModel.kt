package com.yyxnb.arch.vm

import com.yyxnb.arch.TestState
import com.yyxnb.yyxarch.base.mvvm.BaseViewModel

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
