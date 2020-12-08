package top.jfunc.common.http.cookie;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.interceptor.InterceptorAdapter;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.response.ClientHttpResponse;

import java.io.IOException;
import java.util.Objects;

/**
 * 用于处理{@link Cookie}的拦截器
 * @author xiongshiyan at 2020/12/8 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CookieInterceptor extends InterceptorAdapter{
    private CookieAccessor cookieAccessor;

    public CookieInterceptor(CookieAccessor cookieAccessor) {
        this.cookieAccessor = Objects.requireNonNull(cookieAccessor);
    }

    @Override
    public HttpRequest onBefore(HttpRequest httpRequest) throws IOException {
            getCookieAccessor().addCookieIfNecessary(httpRequest);
        return httpRequest;
    }

    @Override
    public ClientHttpResponse onBeforeReturn(HttpRequest httpRequest, ClientHttpResponse clientHttpResponse) throws IOException {
        //必须要获取header
        httpRequest.retainResponseHeaders(Config.RETAIN_RESPONSE_HEADERS);
        getCookieAccessor().saveCookieIfNecessary(httpRequest, clientHttpResponse.getHeaders());
        return clientHttpResponse;
    }

    public CookieAccessor getCookieAccessor() {
        return cookieAccessor;
    }
}
