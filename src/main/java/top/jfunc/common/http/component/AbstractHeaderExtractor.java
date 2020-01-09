package top.jfunc.common.http.component;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.cookie.CookieJar;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * 默认提供cookie的支持
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractHeaderExtractor<S> implements HeaderExtractor<S> {
    @Override
    public MultiValueMap<String, String> extract(S s, HttpRequest httpRequest, String completedUrl) throws IOException {
        Config config = httpRequest.getConfig();

        ///1.如果要支持cookie，必须读取header
        if(null != config.getCookieJar()){
            httpRequest.setIncludeHeaders(HttpRequest.INCLUDE_HEADERS);
        }
        //2.从响应中获取headers
        MultiValueMap<String, String> responseHeaders = doExtractHeaders(s , httpRequest);

        //3.处理cookie
        saveCookieIfNecessary(config.getCookieJar(), completedUrl , responseHeaders);

        return responseHeaders;
    }
    /**
     * 处理cookie相关的
     * @param cookieJar CookieJar
     * @param completedUrl completedUrl
     * @param responseHeaders 响应的headers
     * @throws IOException IOException
     */
    protected void saveCookieIfNecessary(CookieJar cookieJar , String completedUrl, MultiValueMap<String, String> responseHeaders) throws IOException {
        //存入Cookie
        if(null == cookieJar){
            return;
        }
        cookieJar.saveFromResponse(completedUrl, responseHeaders);
    }
    /**
     * 真实的实现获取header
     * @param s S
     * @param httpRequest HttpRequest
     * @return headers
     */
    protected abstract MultiValueMap<String, String> doExtractHeaders(S s , HttpRequest httpRequest);
}
