package top.jfunc.common.http.cookie;

import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultCookieAccessor implements CookieAccessor{
    /**
     * 如果支持Cookie，从CookieHandler中拿出来设置到Header Map中
     * @param httpRequest HttpRequest
     * @throws IOException IOException
     */
    @Override
    public void addCookieIfNecessary(HttpRequest httpRequest) throws IOException {
        CookieJar cookieJar = httpRequest.getConfig().getCookieJar();
        if(null == cookieJar){
            return;
        }

        MultiValueMap<String, String> forRequest = cookieJar.loadForRequest(httpRequest.getCompletedUrl(), httpRequest.getHeaders());

        if(MapUtil.notEmpty(forRequest)){
            forRequest.forEachKeyValue(httpRequest::addHeader);
        }
    }

    /**
     * 如果存在Cookie，将响应的Cookie保存起来
     * @param httpRequest HttpRequest
     * @param responseHeaders 响应的headers
     * @throws IOException IOException
     */
    @Override
    public void saveCookieIfNecessary(HttpRequest httpRequest, MultiValueMap<String, String> responseHeaders) throws IOException {
        CookieJar cookieJar = httpRequest.getConfig().getCookieJar();
        if(null == cookieJar){
            return;
        }
        cookieJar.saveFromResponse(httpRequest.getCompletedUrl(), responseHeaders);
    }
}
