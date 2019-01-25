package com.yyxnb.yyxarch.http.client

import okhttp3.OkHttpClient

/**
 * okHttp client
 */

class HttpClient {

    val builder: OkHttpClient.Builder

    init {
        builder = OkHttpClient.Builder()
    }

    companion object {

        private var instance: HttpClient? = null

        fun getInstance(): HttpClient {

            if (instance == null) {
                synchronized(HttpClient::class.java) {
                    if (instance == null) {
                        instance = HttpClient()
                    }
                }
            }
            return instance!!
        }
    }

}
