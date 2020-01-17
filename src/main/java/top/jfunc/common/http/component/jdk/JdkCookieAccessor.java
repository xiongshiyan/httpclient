package top.jfunc.common.http.component.jdk;

import top.jfunc.common.http.cookie.CookieJar;
import top.jfunc.common.http.cookie.DefaultCookieAccessor;
import top.jfunc.common.http.cookie.JdkCookieJar;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * HttpURLConnection的自己会处理Cookie
 * @author xiongshiyan at 2020/1/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class JdkCookieAccessor extends DefaultCookieAccessor{
    @Override
    public void addCookieIfNecessary(HttpRequest httpRequest) throws IOException {
        CookieJar cookieJar = httpRequest.getConfig().getCookieJar();
        if(null == cookieJar || cookieJar instanceof JdkCookieJar){
            return;
        }

        super.addCookieIfNecessary(httpRequest);
    }

    @Override
    public void saveCookieIfNecessary(HttpRequest httpRequest, MultiValueMap<String, String> responseHeaders) throws IOException {
        CookieJar cookieJar = httpRequest.getConfig().getCookieJar();
        if(null == cookieJar || cookieJar instanceof JdkCookieJar){
            return;
        }

        super.saveCookieIfNecessary(httpRequest , responseHeaders);
    }
}
