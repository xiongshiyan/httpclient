package top.jfunc.common.http.request;

import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ConfigAccessor;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * Http请求的基本定义
 * @since 1.1.6
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface HttpRequest extends ConfigAccessor {
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
    boolean FOLLOW_REDIRECTS = true;

    /**
     * 请求的URL
     * @return 请求的URL
     */
    String getUrl();

    /**
     * 获取真正请求的URL，可能是经过一系列处理的
     * @since 1.2.0
     * @return 真正请求的URL
     */
    String getCompletedUrl();

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
     * 获取设置的路径参数map
     * @return 路径参数，可能为空
     */
    Map<String , String> getRouteParams();

    /**
     * 便捷地设置路径参数
     * @param key key
     * @param value value
     * @return this
     */
    HttpRequest addRouteParam(String key, String value);

    /**
     * 便捷地设置路径参数
     * @param routeParams 多个路径参数
     * @return this
     */
    HttpRequest setRouteParams(Map<String, String> routeParams);

    /**
     * 获取设置的Query参数
     * @return Query参数
     */
    MultiValueMap<String , String> getQueryParams();

    /**
     * 获取Query参数编码
     * @return Query参数编码
     */
    String getQueryParamCharset();

    /**
     * 设置Query参数编码
     * @param paramCharset 参数编码
     * @return this
     */
    HttpRequest setQueryParamCharset(String paramCharset);

    /**
     * 提供便捷的设置Query参数的方法
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    HttpRequest addQueryParam(String key, String value, String... values);

    /**
     * 设置QueryParam参数
     * @param params Param参数
     * @return this
     */
    HttpRequest setQueryParams(MultiValueMap<String, String> params);

    /**
     * 设置QueryParam参数
     * @param params Param参数
     * @return this
     */
    HttpRequest setQueryParams(Map<String, String> params);

    /**
     * 请求的Header
     * @return 请求的Header
     */
    MultiValueMap<String, String> getHeaders();

    /**
     * 便捷的设置header的方法，set方式，相同key就会覆盖
     * @param key key
     * @param value value
     * @return this
     */
    HttpRequest setHeader(String key, String value);

    /**
     * 提供便捷的设置header的方法，add方式，相同key那么value就成为多值
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    HttpRequest addHeader(String key, String value, String... values);

    /**
     * 有些请求可能经过一些处理之后需要改变header重新设置回去
     * @param headers 处理过后的header
     * @return this
     */
    HttpRequest setHeaders(MultiValueMap<String, String> headers);

    /**
     * 设置header
     * @param headers headerHolder
     * @return this
     */
    HttpRequest setHeaders(Map<String, String> headers);

    /**
     * Content-Type
     * @return Content-Type
     */
    String getContentType();

    /**
     * 设置Content-Type
     * 对于HttpURLConnection和OkHttp3来说，Content-Type非常重要，
     * 前者根据Content-Type的编码来输出流，后者直接需要指定Content-Type，
     * 所以建议的最佳实践为：在需要发送body的时候都指定Content-Type
     * @see HttpRequest#setContentType(MediaType)
     * @param contentType Content-Type
     * @return this
     */
    HttpRequest setContentType(String contentType);

    /**
     * 设置Content-Type
     * @see MediaType
     * @param mediaType Content-Type
     * @return this
     */
    default HttpRequest setContentType(MediaType mediaType){
        return setContentType(mediaType.toString());
    }

    /**
     * 连接超时时间 ms
     * -1表示使用系统默认的
     * @return 连接超时时间 ms
     */
    int getConnectionTimeout();

    /**
     * 设置connectionTimeout
     * @param connectionTimeout connectionTimeout -1表示使用系统默认的
     * @return this
     */
    HttpRequest setConnectionTimeout(int connectionTimeout);

    /**
     * 读超时时间 ms
     * -1表示使用系统默认的
     * @return 读超时时间 ms
     */
    int getReadTimeout();

    /**
     * 设置readTimeout
     * @param readTimeout readTimeout -1表示使用系统默认的
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
    boolean followRedirects();

    /**
     * 设置是否支持重定向
     * @param followRedirects 是否支持重定向
     * @return this
     */
    HttpRequest followRedirects(boolean followRedirects);

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
     * HostnameVerifier
     * @return HostnameVerifier
     */
    HostnameVerifier getHostnameVerifier();

    /**
     * 设置HostnameVerifier
     * @param hostnameVerifier HostnameVerifier
     * @return this
     */
    HttpRequest setHostnameVerifier(HostnameVerifier hostnameVerifier);

    /**
     * SSLContext
     * @return SSLContext
     */
    SSLContext getSslContext();

    /**
     * 设置 SSLContext
     * @see SSLSocketFactoryBuilder#getSSLContext()
     * @see SSLSocketFactoryBuilder#getSSLContext(String, String)
     * @see SSLSocketFactoryBuilder#getSSLContext(InputStream, String)
     * @param sslContext SSLContext
     * @return this
     */
    HttpRequest setSslContext(SSLContext sslContext);

    /**
     * SSLSocketFactory
     * 因为一般地 SslSocketFactory 都是从sslContext产生出来的 ， 所以废弃其set方法，从sslContext产生
     * @return SSLSocketFactory
     */
    default SSLSocketFactory getSslSocketFactory(){
        SSLContext sslContext = getSslContext();
        return null == sslContext ? null : sslContext.getSocketFactory();
    }

    /**
     * X509TrustManager
     * @return X509TrustManager
     */
    X509TrustManager getX509TrustManager();

    /**
     * 设置X509TrustManager
     * @param x509TrustManager X509TrustManager
     * @return this
     */
    HttpRequest setX509TrustManager(X509TrustManager x509TrustManager);

    /**
     * 添加属性
     * @param key key
     * @param value value
     * @return this
     */
    HttpRequest addAttribute(String key, Object value);

    /**
     * 获取设置的属性,maybe null
     * @return 属性map
     */
    Map<String , Object> getAttributes();

    /**
     * 获取设置的Method
     * @since 1.2.0
     * @return Method
     */
    Method getMethod();

    /**
     * 设置Method
     * 此方法一般在接口入口处由框架调用，用户自己设置的就以用户的为准，一般不需要设置
     * @since 1.2.0
     * @param method Method
     * @return this
     */
    HttpRequest setMethod(Method method);
}
