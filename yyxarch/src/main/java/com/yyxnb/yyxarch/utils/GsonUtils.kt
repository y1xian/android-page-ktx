package com.yyxnb.yyxarch.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.util.ArrayList
import java.util.HashMap

object GsonUtils {
    /**
     * 将Json数据转化为对象;
     *
     * @param jsonString Json数据;
     * @param cls        转换后的类;
     * @return
     */
    fun <T> getObject(jsonString: String, cls: Class<T>): T? {
        var t: T? = null
        try {
            val gson = Gson()
            t = gson.fromJson(jsonString, cls)
        } catch (e: Exception) {
        }

        return t
    }

    /**
     * 将Json数据转化成List<Object>集合;
     *
     * @param jsonString Json数据;
     * @param cls        将要转化成集合的类;
     * @return
    </Object> */
    fun <T> getArray(jsonString: String, cls: Class<T>): List<T>? {
        var list: List<T> = ArrayList()
        try {
            val gson = Gson()
            list = gson.fromJson(
                    jsonString,
                    object : TypeToken<List<T>>() {

                    }.type
            )
        } catch (e: Exception) {
        }

        return list
    }


    /**
     * 将Json数据转化成List<Map></Map><String></String>, Object>>对象;
     *
     * @param jsonString Json数据;
     * @return
     */
    fun listKeyMaps(jsonString: String): List<Map<String, Any>>? {
        var list: List<Map<String, Any>> = ArrayList()
        try {
            val gson = Gson()
            list = gson.fromJson(
                    jsonString,
                    object : TypeToken<List<Map<String, Any>>>() {

                    }.type
            )
        } catch (e: Exception) {
        }

        return list
    }

    /**
     * 将Json数据转化成Map<String></String>, Object>对象;
     *
     * @param jsonString Json数据;
     * @return
     */
    fun objKeyMaps(jsonString: String): Map<String, Any>? {
        var map: Map<String, Any> = HashMap()
        try {
            val gson = Gson()
            map = gson.fromJson(
                    jsonString,
                    object : TypeToken<Map<String, Any>>() {

                    }.type
            )
        } catch (e: Exception) {
        }

        return map
    }
}
