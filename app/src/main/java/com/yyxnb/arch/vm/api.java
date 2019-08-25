package com.yyxnb.arch.vm;

import android.arch.lifecycle.LiveData;

import com.yyxnb.arch.ApiConstant;
import com.yyxnb.arch.BaseDatas;
import com.yyxnb.arch.TestData;

import java.util.List;
import java.util.Map;

import kotlinx.coroutines.Deferred;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface api {
    /**
     * @param map
     * @return
     */
    @FormUrlEncoded
//    @POST("searchPoetry")
    @POST(ApiConstant.API_UPDATE_APP)
    Deferred<BaseDatas<List<TestData>>> getTeam(@FieldMap Map<String, String> map);

    @GET(ApiConstant.API_TEST_KEY)
    Deferred<BaseDatas<List<TestData>>> getTest();

    @GET(ApiConstant.API_TEST_KEY)
    LiveData<BaseDatas<List<TestData>>> getTest2();
}
