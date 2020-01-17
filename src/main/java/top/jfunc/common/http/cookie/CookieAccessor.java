package top.jfunc.common.http.cookie;

import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface CookieAccessor {
    /**
     * 如果支持Cookie，从CookieHandler中拿出来设置到Header Map中
     * @param httpRequest HttpRequest
     * @throws IOException IOException
     */
    void addCookieIfNecessary(HttpRequest httpRequest) throws IOException;

    /**
     * 如果存在Cookie，将响应的Cookie保存起来
     * @param httpRequest HttpRequest
     * @param responseHeaders 响应的headers
     * @throws IOException IOException
     */
    void saveCookieIfNecessary(HttpRequest httpRequest, MultiValueMap<String, String> responseHeaders) throws IOException;
}
