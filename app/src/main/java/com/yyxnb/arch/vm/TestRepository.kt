package com.yyxnb.arch.vm


import androidx.lifecycle.LiveData
import com.yyxnb.arch.BaseDatas
import com.yyxnb.arch.TestData
import com.yyxnb.yyxarch.base.mvvm.BaseRepository


class TestRepository : BaseRepository<api>() {

    val test: LiveData<BaseDatas<List<TestData>>>
        get() = mApi.test


    suspend fun getTest2(): BaseDatas<List<TestData>> {
        return mApi.test2()
    }


}
