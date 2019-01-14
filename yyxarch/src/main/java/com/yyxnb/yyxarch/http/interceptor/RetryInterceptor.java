package com.yyxnb.yyxarch.http.interceptor;


import com.yyxnb.yyxarch.utils.log.LogUtils;

import java.io.IOException;
import java.io.InterruptedIOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 失败重连 次数、间隔
 */

public class RetryInterceptor implements Interceptor {
    public int executionCount;//最大重试次数  
    private long retryInterval;//重试的间隔  

    RetryInterceptor(Builder builder) {
        this.executionCount = builder.executionCount;
        this.retryInterval = builder.retryInterval;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = doRequest(chain, request);
        int retryNum = 0;
        while ((response == null || !response.isSuccessful()) && retryNum <= executionCount) {
            LogUtils.e("intercept Request is not successful - {}", retryNum);
            final long nextInterval = getRetryInterval();
            try {
                Thread.sleep(nextInterval);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException();
            }
            retryNum++;
            // retry the request  
            response = doRequest(chain, request);
        }
        return response;
    }

    private Response doRequest(Chain chain, Request request) {
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * retry间隔时间
     */
    public long getRetryInterval() {
        return this.retryInterval;
    }

    public static final class Builder {
        private int executionCount;
        private long retryInterval;

        public Builder() {
            executionCount = 3;
            retryInterval = 1000;
        }

        public RetryInterceptor.Builder executionCount(int executionCount) {
            this.executionCount = executionCount;
            return this;
        }

        public RetryInterceptor.Builder retryInterval(long retryInterval) {
            this.retryInterval = retryInterval;
            return this;
        }

        public RetryInterceptor build() {
            return new RetryInterceptor(this);
        }
    }

}