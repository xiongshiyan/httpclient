package top.jfunc.common.http.component.httprequest;

import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

/**
 * @author xiongshiyan at 2020/1/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface HttpRequestFactory {
    /**
     * 创建HttpRequest
     * @return HttpRequest
     */
    HttpRequest create(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset);
}
