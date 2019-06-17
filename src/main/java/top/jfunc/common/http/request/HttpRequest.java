package top.jfunc.common.http.request;

import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.holder.HeaderHolder;
import top.jfunc.common.http.holder.ParamHolder;
import top.jfunc.common.http.holder.RouteParamHolder;
import top.jfunc.common.http.holder.SSLHolder;

import java.net.URL;

/**
 * Http请求的基本定义
 * @since 1.1
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface HttpRequest{
    /**
     * 结果包含headers
     */
    boolean INCLUDE_HEADERS = true;
    /**
     * 结果忽略body
     */
    boolean IGNORE_RESPONSE_BODY = true;
    /**
     * 支持重定向
     */
    boolean REDIRECTABLE = true;

    /**
     * 请求的URL
     * @return 请求的URL
     */
    String getUrl();

    /**
     * 设置URL
     * @param url url
     * @return this
     */
    HttpRequest setUrl(String url);

    /**
     * 设置URL
     * @param url URL
     * @return this
     */
    default HttpRequest setUrl(URL url){
        return setUrl(url.toString());
    }

    /**
     *获取到 {@link RouteParamHolder} 可以对路径参数完全接管处理
     * @return RouteParamHolder must not be null
     */
    RouteParamHolder routeParamHolder();

    /**
     * 还是提供一些便捷方法
     * @param key key
     * @param value value
     * @return this
     */
    default HttpRequest addRouteParam(String key, String value){
        routeParamHolder().addRouteParam(key, value);
        return this;
    }

    /**
     * 获取到 {@link ParamHolder} 可以对Query参数完全接管处理
     * @return ParamHolder must not be null
     */
    ParamHolder queryParamHolder();

    /**
     * 提供便捷方法
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    default HttpRequest addQueryParam(String key, String value, String... values){
        queryParamHolder().addParam(key, value, values);
        return this;
    }

    /**
     * 获取到 {@link HeaderHolder} 可以对Header完全接管处理
     * @return HeaderHolder must not be null
     */
    HeaderHolder headerHolder();

    /**
     * 提供便捷方法
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    default HttpRequest addHeader(String key, String value, String... values){
        headerHolder().addHeader(key, value, values);
        return this;
    }

    /**
     * Content-Type
     * @return Content-Type
     */
    String getContentType();

    /**
     * 设置Content-Type
     * @param contentType Content-Type
     * @return this
     */
    HttpRequest setContentType(String contentType);

    /**
     * 设置Content-Type
     * @param mediaType Content-Type
     * @return this
     */
    default HttpRequest setContentType(MediaType mediaType){
        return setContentType(mediaType.toString());
    }

    /**
     * 连接超时时间 ms
     * @return 连接超时时间 ms
     */
    Integer getConnectionTimeout();

    /**
     * 设置connectionTimeout
     * @param connectionTimeout connectionTimeout
     * @return this
     */
    HttpRequest setConnectionTimeout(int connectionTimeout);

    /**
     * 读超时时间 ms
     * @return 读超时时间 ms
     */
    Integer getReadTimeout();

    /**
     * 设置readTimeout
     * @param readTimeout readTimeout
     * @return this
     */
    HttpRequest setReadTimeout(int readTimeout) ;

    /**
     * 结果字符编码
     * @return 结果字符编码
     */
    String getResultCharset();

    /**
     * 设置resultCharset
     * @param resultCharset resultCharset
     * @return this
     */
    HttpRequest setResultCharset(String resultCharset);

    /**
     * 响应中是否包含header
     * @return 响应中是否包含header
     */
    boolean isIncludeHeaders();

    /**
     * 设置includeHeaders
     * @param includeHeaders includeHeaders
     * @return this
     */
    HttpRequest setIncludeHeaders(boolean includeHeaders);

    /**
     * 是否忽略响应体，在不需要响应体的场景下提高效率
     * @return 是否忽略响应体
     */
    boolean isIgnoreResponseBody();

    /**
     * 设置ignoreResponseBody
     * @param ignoreResponseBody ignoreResponseBody
     * @return this
     */
    HttpRequest setIgnoreResponseBody(boolean ignoreResponseBody);

    /**
     * 是否重定向
     * @return 是否重定向
     */
    boolean isRedirectable();

    /**
     * 设置是否支持重定向
     * @param redirectable 是否支持重定向
     * @return this
     */
    HttpRequest setRedirectable(boolean redirectable);

    /**
     * 代理信息
     * @see java.net.Proxy
     * @return 代理信息
     */
    ProxyInfo getProxyInfo();

    /**
     * 设置proxyInfo
     * @param proxyInfo proxyInfo
     * @return this
     */
    HttpRequest setProxy(ProxyInfo proxyInfo);

    /**
     * SSL相关设置的处理器
     * @return SSLHolder must not be null
     */
    SSLHolder sslHolder();
}
