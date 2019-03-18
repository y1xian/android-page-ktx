package com.yyxnb.yyxarch.http;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Description: 多BaseUrl解析器
 */
public interface IUrlParser {

    /**
     * 将 {@link RetrofitMultiUrl#mBaseUrlMap} 中映射的 Url 解析成完整的{@link HttpUrl}
     * 用来替换 @{@link Request#url} 里的BaseUrl以达到动态切换 Url的目的
     *
     * @param domainUrl 目标请求(base url)
     * @param url       需要替换的请求(原始url)
     * @return
     */
    HttpUrl parseUrl(HttpUrl domainUrl, HttpUrl url);
}
