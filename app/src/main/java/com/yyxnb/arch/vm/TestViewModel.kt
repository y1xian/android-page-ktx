package com.yyxnb.arch.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.yyxnb.arch.BaseDatas
import com.yyxnb.arch.TestData
import com.yyxnb.yyxarch.base.mvvm.BaseViewModel
import com.yyxnb.yyxarch.bean.Lcee
import com.yyxnb.yyxarch.livedata.SingleLiveEvent
import io.reactivex.annotations.NonNull
import java.util.*

class TestViewModel(@NonNull application: Application) : BaseViewModel<TestRepository>(application) {

    private val reqTeam = SingleLiveEvent<Map<String,String>>()
    private val reqTeam2 = SingleLiveEvent<Map<String,String>>()
    private val reqTest = MutableLiveData<Map<String,String>>()

    val team: LiveData<Lcee<BaseDatas<List<TestData>>>>
        get() = Transformations.switchMap(reqTeam) { input -> mRepository.getTeam(input) }

    val team2: LiveData<Lcee<BaseDatas<List<TestData>>>>
        get() = Transformations.switchMap(reqTeam2) { input -> mRepository.getTeam2(input) }

    //        return mRepository.getTest();
    val test: LiveData<BaseDatas<List<TestData>>>
        get() = Transformations.switchMap(reqTest) { _ -> mRepository.test }


    fun reqTeam() {
        val map = LinkedHashMap<String, String>()
        map["name"] = "李白"
        reqTeam.value = map
    }

    fun reqTeam2() {
        val map = LinkedHashMap<String, String>()
        map["name"] = "杜甫"
        reqTeam2.value = map
    }

    fun reqTest() {
        val map = LinkedHashMap<String, String>()
        map["name"] = "杜甫"
        reqTest.value = map


    }

}
