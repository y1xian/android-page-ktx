package com.yyxnb.arch.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.yyxnb.arch.BaseDatas
import com.yyxnb.arch.TestData
import com.yyxnb.arch.TestState
import com.yyxnb.yyxarch.base.mvvm.BaseViewModel
import com.yyxnb.yyxarch.bean.Lcee
import com.yyxnb.yyxarch.livedata.SingleLiveEvent
import java.util.*

class TestViewModel() : BaseViewModel<TestState,TestRepository>(TestState()) {

    private val reqTeam = SingleLiveEvent<Map<String,String>>()
    private val reqTest = MutableLiveData<Map<String,String>>()

    val team: LiveData<Lcee<BaseDatas<List<TestData>>>>
        get() = Transformations.switchMap(reqTeam) { input -> mRepository.getTeam(input) }

    //        return mRepository.getTest();
    val test: LiveData<BaseDatas<List<TestData>>>
        get() = Transformations.switchMap(reqTest) { _ -> mRepository.test }


    fun reqTeam() {
        val map = LinkedHashMap<String, String>()
        map["name"] = "李白"
        reqTeam.value = map
    }

    fun reqTest() {
        val map = LinkedHashMap<String, String>()
        map["name"] = "杜甫"
        reqTest.value = map


    }

}
