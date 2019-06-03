package top.jfunc.common.http.request;

import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.holder.HeaderHolder;
import top.jfunc.common.http.holder.ParamHolder;
import top.jfunc.common.http.holder.RouteParamHolder;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.net.URL;
import java.util.Map;

/**
 * Http请求的基本定义
 * @since 1.1
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface HttpRequest {
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
     * @return this
     */
    HttpRequest setUrl(String url);

    /**
     * 设置URL
     * @return this
     */
    default HttpRequest setUrl(URL url){
        return setUrl(url.toString());
    }

    /**
     * 路径参数
     * @return 路径参数
     */
    default Map<String, String> getRouteParams(){
        return routeParamHolder().getRouteParams();
    }

    /**
     *获取到 {@link RouteParamHolder} 可以对路径参数完全接管处理
     * @return RouteParamHolder must not be null
     */
    RouteParamHolder routeParamHolder();

    /**
     * Query参数
     * @return Query参数
     */
    default MultiValueMap<String, String> getQueryParams(){
        return queryParamHolder().getParams();
    }

    /**
     * 获取到 {@link ParamHolder} 可以对Query参数完全接管处理
     * @return ParamHolder must not be null
     */
    ParamHolder queryParamHolder();

    /**
     * 请求的Header
     * @return 请求的Header
     */
    default MultiValueMap<String, String> getHeaders(){
        return headerHolder().getHeaders();
    }

    /**
     * 获取到 {@link HeaderHolder} 可以对Header完全接管处理
     * @return HeaderHolder must not be null
     */
    HeaderHolder headerHolder();

    /**
     * Content-Type
     * @return Content-Type
     */
    String getContentType();

    /**
     * 连接超时时间 ms
     * @return 连接超时时间 ms
     */
    Integer getConnectionTimeout();

    /**
     * 读超时时间 ms
     * @return 读超时时间 ms
     */
    Integer getReadTimeout();

    /**
     * 请求体编码，也可作为Query参数编码
     * @return 编码字符串
     */
    String getBodyCharset();

    /**
     * 结果字符编码
     * @return 结果字符编码
     */
    String getResultCharset();

    /**
     * 响应中是否包含header
     * @return 响应中是否包含header
     */
    boolean isIncludeHeaders();

    /**
     * 是否忽略响应体，在不需要响应体的场景下提高效率
     * @return 是否忽略响应体
     */
    boolean isIgnoreResponseBody();

    /**
     * 是否重定向
     * @return 是否重定向
     */
    boolean isRedirectable();

    /**
     * 代理信息
     * @see java.net.Proxy
     * @return 代理信息
     */
    ProxyInfo getProxyInfo();


    /**
     * HostnameVerifier
     * @return HostnameVerifier
     */
    HostnameVerifier getHostnameVerifier();

    /**
     * SSLContext
     * @return SSLContext
     */
    SSLContext getSslContext();

    /**
     * SSLSocketFactory
     * @return SSLSocketFactory
     */
    SSLSocketFactory getSslSocketFactory();

    /**
     * X509TrustManager
     * @return X509TrustManager
     */
    X509TrustManager getX509TrustManager();
}
