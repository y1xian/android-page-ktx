package com.yyxnb.arch.vm

import androidx.lifecycle.LiveData

import com.yyxnb.arch.ApiConstant
import com.yyxnb.arch.BaseDatas
import com.yyxnb.arch.TestData

import kotlinx.coroutines.Deferred
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface api {

    @get:GET(ApiConstant.API_TEST_KEY)
    val test: LiveData<BaseDatas<List<TestData>>>

    @GET(ApiConstant.API_TEST_KEY)
    suspend fun test2(): BaseDatas<List<TestData>>

    @FormUrlEncoded
    @POST("searchPoetry")
    fun getTeam(@FieldMap map: Map<String, String>): Deferred<BaseDatas<List<TestData>>>
}
