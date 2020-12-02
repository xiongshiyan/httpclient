package top.jfunc.common.http.component.httprequest;

import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.util.Map;

/**
 * @author xiongshiyan at 2020/1/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface StringBodyHttpRequestFactory{
    /**
     * 根据给定的相关参数组装为一个{@link StringBodyRequest}，
     * 支持{@link top.jfunc.common.http.base.Method#POST}等含有body的请求
     * @see top.jfunc.common.http.smart.AbstractSmartHttpClient#post(String, String, String, Map, int, int, String, String)
     * @param url URL
     * @param queryParams 查询参数 maybe null
     * @param body body
     * @param contentType ContentType
     * @param headers header maybe null
     * @param connectTimeout 连接超时
     * @param readTimeout 读取超时
     * @param bodyCharset 字符集 maybe null
     * @param resultCharset 结果字符集 maybe null
     * @return HttpRequest HttpRequest
     */
    StringBodyRequest create(String url,
                             MultiValueMap<String, String> queryParams,
                             String body,
                             String contentType,
                             MultiValueMap<String, String> headers,
                             int connectTimeout,
                             int readTimeout,
                             String bodyCharset,
                             String resultCharset);
}
