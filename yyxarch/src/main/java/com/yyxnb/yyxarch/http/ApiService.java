package com.yyxnb.yyxarch.http;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> post(@Url String url, @FieldMap Map<String, String> maps);


    @POST()
    Observable<ResponseBody> postBody(@Url String url, @Body Object object);


    @POST()
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<ResponseBody> postJson(@Url String url, @Body RequestBody jsonBody);


    @POST()
    Observable<ResponseBody> postBody(@Url String url, @Body RequestBody body);


    @GET()
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, String> maps);


    @DELETE()
    Observable<ResponseBody> delete(@Url String url, @QueryMap Map<String, String> maps);


    @DELETE()
    Observable<ResponseBody> deleteBody(@Url String url, @Body Object object);


    @DELETE()
    Observable<ResponseBody> deleteBody(@Url String url, @Body RequestBody body);


    @DELETE()
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<ResponseBody> deleteJson(@Url String url, @Body RequestBody jsonBody);


    @PUT()
    Observable<ResponseBody> put(@Url String url, @QueryMap Map<String, String> maps);


    @PUT()
    Observable<ResponseBody> putBody(@Url String url, @Body Object object);


    @Multipart
    @POST()
    Observable<ResponseBody> uploadFlie(@Url String fileUrl, @Part("description") RequestBody description, @Part("files") MultipartBody.Part file);


    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url String url, @PartMap() Map<String, RequestBody> maps);


    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url String url, @Part() List<MultipartBody.Part> parts);


    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);
}
