package com.yyxnb.arch;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface api {
    /**
     *
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST("searchPoetry")
    Observable<BaseDatas<List<TestData>>> getTeam(@FieldMap Map<String, String> map);
}
