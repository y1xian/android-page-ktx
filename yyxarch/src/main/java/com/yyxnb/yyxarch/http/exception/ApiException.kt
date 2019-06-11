package com.yyxnb.yyxarch.http.exception

import com.google.gson.JsonParseException
import com.google.gson.JsonSerializer
import com.google.gson.JsonSyntaxException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.io.NotSerializableException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * 统一处理了API异常错误
 */

class ApiException(throwable: Throwable, var code: Int?) : Exception(throwable) {

    override var message: String? = null

    init {
        this.message = throwable.message
    }

    /**
     * 约定异常
     */
    object ERROR {
        /**
         * 未知错误
         */
        const val UNKNOWN = 1000
        /**
         * 连接超时
         */
        const val TIMEOUT_ERROR = 1001
        /**
         * 空指针错误
         */
        const val NULL_POINTER_EXCEPTION = 1002

        /**
         * 证书出错
         */
        const val SSL_ERROR = 1003

        /**
         * 类转换错误
         */
        const val CAST_ERROR = 1004

        /**
         * 解析错误
         */
        const val PARSE_ERROR = 1005

        /**
         * 非法数据异常
         */
        const val ILLEGAL_STATE_ERROR = 1006

        /**
         * 未知主机错误
         */
        const val UNKNOWNHOST_ERROR = 1007

    }

    companion object {

        fun handleException(e: Throwable): ApiException {
            val ex: ApiException
            if (e is HttpException) {
                ex = ApiException(e, e.code())
                try {
                    ex.message = "httpException " + e.response()?.errorBody()!!.string()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                    ex.message = "IOException " + e1.message
                }

                return ex
            } else if (e is SocketTimeoutException) {
                ex = ApiException(e, ERROR.TIMEOUT_ERROR)
                ex.message = "网络连接超时，请检查您的网络状态，稍后重试！"
                return ex
            } else if (e is ConnectException) {
                ex = ApiException(e, ERROR.TIMEOUT_ERROR)
                ex.message = "网络连接异常，请检查您的网络状态，稍后重试！"
                return ex
            } else if (e is ConnectTimeoutException) {
                ex = ApiException(e, ERROR.TIMEOUT_ERROR)
                ex.message = "网络连接超时，请检查您的网络状态，稍后重试！"
                return ex
            } else if (e is NullPointerException) {
                ex = ApiException(e, ERROR.NULL_POINTER_EXCEPTION)
                ex.message = "空指针异常"
                return ex
            } else if (e is javax.net.ssl.SSLHandshakeException) {
                ex = ApiException(e, ERROR.SSL_ERROR)
                ex.message = "证书验证失败"
                return ex
            } else if (e is ClassCastException) {
                ex = ApiException(e, ERROR.CAST_ERROR)
                ex.message = "类型转换错误"
                return ex
            } else if (e is ServerException) {
                ex = ApiException(e, e.errCode)
                ex.message = e.message
                return ex
            } else if (e is JsonParseException
                    || e is JSONException
                    || e is JsonSyntaxException
                    || e is JsonSerializer<*>
                    || e is NotSerializableException
                    || e is ParseException) {
                ex = ApiException(e, ERROR.PARSE_ERROR)
                ex.message = "解析错误"
                return ex
            } else if (e is IllegalStateException) {
                ex = ApiException(e, ERROR.ILLEGAL_STATE_ERROR)
                ex.message = e.message
                return ex
            } else if (e is UnknownHostException) {
                ex = ApiException(e, ERROR.UNKNOWNHOST_ERROR)
                ex.message = "无法解析该域名"
                return ex
            } else {
                ex = ApiException(e, ERROR.UNKNOWN)
                ex.message = "未知错误"
                return ex
            }
        }
    }
}
