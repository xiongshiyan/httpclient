package top.jfunc.common.http.component.httprequest;

import top.jfunc.common.http.request.MutableStringBodyRequest;

import java.util.Map;

/**
 * @author xiongshiyan at 2020/1/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface StringBodyHttpRequestFactory{
    MutableStringBodyRequest create(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset);
}
