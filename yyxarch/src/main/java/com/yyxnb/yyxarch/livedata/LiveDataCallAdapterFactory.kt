package com.yyxnb.yyxarch.livedata

import androidx.lifecycle.LiveData
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory : Factory() {
    /**
     * 如果你要返回
     * LiveData<?>
     */
    override fun get(returnType: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*, *>? {
        if (returnType !is ParameterizedType) {
            throw IllegalArgumentException("返回值需为参数化类型")
        }
        //获取returnType的class类型
        val returnClass = getRawType(returnType)
        if (returnClass != LiveData::class.java) {
            throw IllegalArgumentException("返回值不是LiveData类型")
        }
        //先解释一下getParameterUpperBound
        //官方例子
        //For example, index 1 of {@code Map<String, ? extends Runnable>} returns {@code Runnable}.
        //获取的是Map<String,? extends Runnable>参数列表中index序列号的参数类型,即0为String,1为Runnable
        //这里的0就是LiveData<?>中?的序列号,因为只有一个参数
        //其实这个就是我们请求返回的实体
        val type = getParameterUpperBound(0, returnType)
        return LiveDataCallAdapter<Any>(type)
    }
}
