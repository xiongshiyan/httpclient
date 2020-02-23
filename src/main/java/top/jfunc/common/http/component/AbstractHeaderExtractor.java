package top.jfunc.common.http.component;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * 默认提供cookie的支持
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractHeaderExtractor<S> implements HeaderExtractor<S> {
    /**
     * @param s 请求响应
     * @param httpRequest HttpRequest
     * @return 可能返回null
     * @throws IOException IOException
     */
    @Override
    public MultiValueMap<String, String> extract(S s, HttpRequest httpRequest) throws IOException {
        Config config = httpRequest.getConfig();

        boolean retainResponseHeaders = config.retainResponseHeadersWithDefault(httpRequest.retainResponseHeaders());
        boolean followRedirects = config.followRedirectsWithDefault(httpRequest.followRedirects());

        ///1.如果要支持cookie，必须读取header
        if(null != config.getCookieJar() || followRedirects){
            retainResponseHeaders = Config.RETAIN_RESPONSE_HEADERS;
        }

        //要求不需要解析header
        if(!retainResponseHeaders){
            return null;
        }

        //2.从响应中获取headers
        return doExtractHeaders(s , httpRequest);
    }
    /**
     * 真实的实现获取header
     * @param s S
     * @param httpRequest HttpRequest
     * @return headers
     * @throws IOException IOException
     */
    protected abstract MultiValueMap<String, String> doExtractHeaders(S s , HttpRequest httpRequest) throws IOException;
}
