package top.jfunc.common.http.component;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.http.cookie.CookieJar;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.CollectionUtil;
import top.jfunc.common.utils.Joiner;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.util.List;

/**
 * 提供统一处理cookie的方法
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractHeaderHandler<C> implements HeaderHandler<C> {
    @Override
    public void configHeaders(C target, HttpRequest httpRequest) throws IOException {
        Config config = httpRequest.getConfig();
        //1.合并默认headers
        MultiValueMap<String, String> handledHeaders = config.mergeDefaultHeaders(httpRequest.getHeaders());

        //2.处理cookie
        handledHeaders = addCookieIfNecessary(config.getCookieJar() , httpRequest.getCompletedUrl(), handledHeaders);

        //3.真正设置
        doConfigHeaders(target , httpRequest , handledHeaders);
    }
    /**
     * 如果支持Cookie，从CookieHandler中拿出来设置到Header Map中
     * @param cookieJar CookieJar
     * @param completedUrl URL
     * @param requestHeaders 正常用户的Header Map
     * @return 处理过的Header Map
     * @throws IOException IOException
     */
    protected MultiValueMap<String, String> addCookieIfNecessary(CookieJar cookieJar , String completedUrl, MultiValueMap<String, String> requestHeaders) throws IOException {
        if(null == cookieJar){
            return requestHeaders;
        }

        List<String> cookies = cookieJar.loadForRequest(completedUrl , requestHeaders);
        if(CollectionUtil.isEmpty(cookies)){
            return requestHeaders;
        }

        if(null == requestHeaders){
            requestHeaders = new ArrayListMultiValueMap<>();
        }
        requestHeaders.add(HttpHeaders.COOKIE, Joiner.on(HttpConstants.SEMICOLON).join(cookies));

        return requestHeaders;
    }
    /**
     * 真正的设置header
     * @param c C
     * @param httpRequest HttpRequest
     * @param handledHeaders 处理过后的headers
     * @throws IOException IOException
     */
    protected abstract void doConfigHeaders(C c, HttpRequest httpRequest , MultiValueMap<String , String> handledHeaders) throws IOException;
}
