package com.yyxnb.arch.vm

import androidx.lifecycle.viewModelScope
import com.yyxnb.arch.TestState
import com.yyxnb.yyxarch.base.mvvm.BaseViewModel
import kotlinx.coroutines.launch

class Test1ViewModel : BaseViewModel<TestState,TestRepository>(TestState()) {

    fun getTest() = viewModelScope.launch {

        setState {
            copy(isLoading = true)
        }

        val data = mRepository.getTest2()

        setState {
            copy(data = data, isLoading = false)
        }
    }


}
