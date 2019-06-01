package top.jfunc.common.http.request;

import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.kv.Header;
import top.jfunc.common.http.kv.Parameter;
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
    HttpRequest setUrl(URL url);

    /**
     * 路径参数
     * @return 路径参数
     */
    Map<String, String> getRouteParams();

    /**
     * 设置路径参数
     * @param routeParams 路径参数映射
     * @return this
     */
    HttpRequest setRouteParams(Map<String, String> routeParams);

    /**
     * 添加路径参数
     * @param key key
     * @param value value
     */
    HttpRequest addRouteParam(String key, String value);

    /**
     * Query参数
     * @return Query参数
     */
    MultiValueMap<String, String> getQueryParams();

    /**
     * 设置Query参数
     * @param queryParams Query参数
     * @return this
     */
    HttpRequest setQueryParams(MultiValueMap<String, String> queryParams);

    /**
     * 设置Query参数
     * @param queryParams Query参数
     * @return this
     */
    HttpRequest setQueryParams(Map<String, String> queryParams);

    /**
     * 添加Query参数
     * @param key key
     * @param value value
     */
    HttpRequest addQueryParam(String key, String value);

    /**
     * 添加Query参数
     * @param key key
     * @param value value
     * @param values values
     */
    HttpRequest addQueryParam(String key, String value, String... values);

    /**
     * 添加Query参数
     * @param key key
     * @param values values
     * @return this
     */
    HttpRequest addQueryParam(String key, Iterable<String> values);

    /**
     * 添加Query参数
     * @param parameter parameter
     * @param parameters parameters
     * @return this
     */
    HttpRequest addQueryParam(Parameter parameter , Parameter... parameters);

    /**
     * 添加Query参数
     * @param parameters parameters
     * @return this
     */
    HttpRequest addQueryParam(Iterable<Parameter> parameters);

    /**
     * 添加Query参数
     * @param parameter parameter
     * @param parameters parameters
     * @return this
     */
    HttpRequest addQueryParam(Map.Entry<String , Iterable<String>> parameter , Map.Entry<String , Iterable<String>>... parameters);

    /**
     * 请求的Header
     * @return 请求的Header
     */
    MultiValueMap<String, String> getHeaders();

    /**
     * 有些请求可能经过一些处理之后需要改变header重新设置回去
     * @param headers 处理过后的header
     * @return this
     */
    HttpRequest setHeaders(MultiValueMap<String, String> headers);

    /**
     * 设置header
     * @param headers headers
     * @return this
     */
    HttpRequest setHeaders(Map<String, String> headers);

    /**
     * 添加header
     * @param key key
     * @param value value
     * @return this
     */
    HttpRequest addHeader(String key, String value);

    /**
     * 添加header
     * @param key key
     * @param value value
     * @param values values
     */
    HttpRequest addHeader(String key, String value, String... values);

    /**
     * 添加header
     * @param key key
     * @param values values
     * @return this
     */
    HttpRequest addHeader(String key, Iterable<String> values);

    /**
     * 添加header
     * @param header header
     * @param headers headers
     * @return this
     */
    HttpRequest addHeader(Header header , Header... headers);

    /**
     * 添加header
     * @param headers headers
     * @return this
     */
    HttpRequest addHeader(Iterable<Header> headers);

    /**
     * 添加header
     * @param header header
     * @param headers headers
     * @return this
     */
    HttpRequest addHeader(Map.Entry<String , Iterable<String>> header , Map.Entry<String , Iterable<String>>... headers);

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
