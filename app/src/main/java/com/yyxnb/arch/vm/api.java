package com.yyxnb.arch.vm;

import com.yyxnb.arch.ApiConstant;
import com.yyxnb.arch.BaseDatas;
import com.yyxnb.arch.TestData;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import kotlinx.coroutines.Deferred;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface api {
    /**
     * @param map
     * @return
     */
    @FormUrlEncoded
//    @POST("searchPoetry")
    @POST(ApiConstant.API_UPDATE_APP)
//    @Headers({RetrofitMultiUrl.BASE_URL_NAME_HEADER + ApiConstant.API_UPDATE_APP_KEY})
    Observable<BaseDatas<List<TestData>>> getTeam(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("searchPoetry")
    Deferred<BaseDatas<List<TestData>>> getTeam2(@FieldMap Map<String, String> map);
}
