package com.yyxnb.yyxarch.http.upload


import com.yyxnb.yyxarch.http.client.RetrofitClient
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*

/**
 * 为上传单独建一个retrofit
 */

class UploadRetrofit {
    val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
    }

    companion object {

        private var instance: UploadRetrofit? = null

        private val baseUrl = "https://api.github.com/"

        fun getInstance(): UploadRetrofit {

            if (instance == null) {
                synchronized(RetrofitClient::class.java) {
                    if (instance == null) {
                        instance = UploadRetrofit()
                    }
                }

            }
            return instance!!
        }


        /**
         * 上传一张图片
         *
         * @param uploadUrl 上传图片的服务器url
         * @param filePath  图片路径
         * @return Observable
         */
        fun uploadImg(uploadUrl: String, filePath: String): Observable<ResponseBody> {
            val filePaths = ArrayList<String>()
            filePaths.add(filePath)
            return uploadImgsWithParams(uploadUrl, "uploaded_file", null, filePaths)

        }

        /**
         * 只上传图片
         *
         * @param uploadUrl 上传图片的服务器url
         * @param filePaths 图片路径
         * @return Observable
         */
        fun uploadImgs(uploadUrl: String, filePaths: List<String>): Observable<ResponseBody> {
            return uploadImgsWithParams(uploadUrl, "uploaded_file", null, filePaths)
        }

        /**
         * 图片和参数同时上传的请求
         *
         * @param uploadUrl 上传图片的服务器url
         * @param fileName  后台协定的接受图片的name（没特殊要求就可以随便写）
         * @param map       普通参数
         * @param filePaths 图片路径
         * @return Observable
         */
        fun uploadImgsWithParams(uploadUrl: String, fileName: String, map: Map<String, Any>?, filePaths: List<String>): Observable<ResponseBody> {

            val builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)

            if (null != map) {
                for (key in map.keys) {
                    builder.addFormDataPart(key, (map[key] as String?)!!)
                }
            }

            for (i in filePaths.indices) {
                val file = File(filePaths[i])
                val imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                //"medias"+i 后台接收图片流的参数名
                builder.addFormDataPart(fileName, file.name, imageBody)
            }

            val parts = builder.build().parts()

            return UploadRetrofit
                    .getInstance()
                    .retrofit
                    .create(IUploadFileApi::class.java)
                    .uploadImgs(uploadUrl, parts)
        }
    }
}
