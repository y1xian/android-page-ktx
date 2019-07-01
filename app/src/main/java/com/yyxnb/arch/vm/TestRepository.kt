package com.yyxnb.arch.vm


import androidx.lifecycle.LiveData
import com.yyxnb.arch.BaseDatas
import com.yyxnb.arch.TestData

import com.yyxnb.yyxarch.base.mvvm.BaseRepository
import com.yyxnb.yyxarch.bean.Lcee
import com.yyxnb.yyxarch.livedata.LiveDataObservableAdapter

class TestRepository : BaseRepository<api>() {

    val test: LiveData<BaseDatas<List<TestData>>>
        get() = mApi.test


    fun getTeam(map: Map<String, String>): LiveData<Lcee<BaseDatas<List<TestData>>>> {
        return LiveDataObservableAdapter.fromObservableLcee(mApi.getTeam(map))
    }

    fun getTeam2(map: Map<String, String>): LiveData<Lcee<BaseDatas<List<TestData>>>> {

        return LiveDataObservableAdapter.fromDeferredLcee(mApi.getTeam2(map))
    }
}
