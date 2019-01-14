package com.yyxnb.yyxarch.http.interceptor;

import com.yyxnb.yyxarch.utils.JsonUtils;
import com.yyxnb.yyxarch.utils.log.LogUtils;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Description: 日志打印格式化处理
 *
 * @author : yyx
 * @date ：2018/6/16
 */
public class RxHttpLogger implements HttpLoggingInterceptor.Logger {
    private StringBuilder mMessage = new StringBuilder();

    @Override
    public void log(String message) {
        // 请求或者响应开始
        if (message.startsWith("--> POST")) {
            mMessage.setLength(0);
            mMessage.append(" ");
            mMessage.append("\r\n");
        }
        if (message.startsWith("--> GET")) {
            mMessage.setLength(0);
            mMessage.append(" ");
            mMessage.append("\r\n");
        }
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if ((message.startsWith("{") && message.endsWith("}"))
                || (message.startsWith("[") && message.endsWith("]"))) {
            message = JsonUtils.formatJson(JsonUtils.decodeUnicode(message));
        }
        mMessage.append(message.concat("\n"));
        // 请求或者响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP")) {
            LogUtils.d( mMessage.toString());
        }
    }
}