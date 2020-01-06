package top.jfunc.common.http.request.basic;

import top.jfunc.common.ChainCall;
import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author xiongshiyan at 2019/7/5 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class BaseHttpRequest<THIS extends BaseHttpRequest> implements HttpRequest, ChainCall<THIS>{
    /**
     * 设置的URL
     */
    private String url;
    /**
     * 路径参数
     */
    private Map<String , String> routeParams;
    /**
     * Query参数
     */
    private MultiValueMap<String , String> queryParams;
    /**
     * Query参数字符编码
     */
    private String queryParamCharset = HttpConstants.DEFAULT_CHARSET;
    /**
     * header，可能多值
     */
    private MultiValueMap<String , String> headers;
    /**
     * 资源类型
     */
    private String contentType = null;
    /**
     * 连接超时时间，不设置就使用系统默认的
     * @see top.jfunc.common.http.base.Config#defaultConnectionTimeout
     */
    private int connectionTimeout = HttpConstants.TIMEOUT_UNSIGNED;
    /**
     * 读数据超时时间，不设置就使用系统默认的
     * @see top.jfunc.common.http.base.Config#defaultReadTimeout
     */
    private int readTimeout = HttpConstants.TIMEOUT_UNSIGNED;
    /**
     * 返回体编码，不设置就使用系统默认的
     * @see top.jfunc.common.http.base.Config#defaultResultCharset
     */
    private String resultCharset = HttpConstants.DEFAULT_CHARSET;
    /**
     * 返回结果中是否包含headers,默认不包含
     */
    private boolean includeHeaders = !INCLUDE_HEADERS;
    /**
     * 返回结果中是否忽略body,  true那么就不去读取body，提高效率, 默认不忽略
     */
    private boolean ignoreResponseBody = !IGNORE_RESPONSE_BODY;
    /**
     * 是否支持重定向
     */
    private boolean followRedirects = !FOLLOW_REDIRECTS;
    /**
     * 代理设置,如果有就设置
     * Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port));
     */
    private ProxyInfo proxyInfo = null;

    /**
     * HostnameVerifier
     */
    private HostnameVerifier hostnameVerifier = null;
    /**
     * SSLContext
     */
    private SSLContext sslContext = null;
    /**
     * X509TrustManager
     */
    private X509TrustManager x509TrustManager = null;
    /**
     * 属性设置
     */
    private Map<String , Object> attributes;


    /**用于接收系统的默认设置*/
    private Config config;

    public BaseHttpRequest(String url){this.url = url;}
    public BaseHttpRequest(URL url){this.url = url.toString();}
    public BaseHttpRequest(){}

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public THIS setUrl(String url) {
        this.url = url;
        return myself();
    }

    @Override
    public HttpRequest setUrl(URL url) {
        setUrl(url.toString());
        return myself();
    }

    @Override
    public Map<String, String> getRouteParams() {
        return routeParams;
    }

    @Override
    public THIS addRouteParam(String key, String value) {
        if(null == routeParams){
            routeParams = new HashMap<>(2);
        }
        routeParams.put(key, value);
        return myself();
    }

    @Override
    public THIS setRouteParams(Map<String, String> routeParams) {
        this.routeParams = routeParams;
        return myself();
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return queryParams;
    }

    @Override
    public String getQueryParamCharset() {
        return queryParamCharset;
    }

    @Override
    public THIS setQueryParamCharset(String paramCharset) {
        this.queryParamCharset = paramCharset;
        return myself();
    }

    @Override
    public THIS addQueryParam(String key, String value, String... values) {
        if(null == queryParams){
            queryParams = new ArrayListMultiValueMap<>(2);
        }
        queryParams.add(key, value, values);
        return myself();
    }

    @Override
    public THIS setQueryParams(MultiValueMap<String, String> params) {
        this.queryParams = Objects.requireNonNull(params);
        return myself();
    }

    @Override
    public THIS setQueryParams(Map<String, String> params) {
        this.queryParams = ArrayListMultiValueMap.fromMap(Objects.requireNonNull(params));
        return myself();
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public THIS setHeader(String key, String value) {
        initHeaders();
        headers.set(key, value);
        return myself();
    }

    @Override
    public THIS addHeader(String key, String value, String... values) {
        initHeaders();
        headers.add(key, value, values);
        return myself();
    }

    @Override
    public HttpRequest setHeaders(MultiValueMap<String, String> headers) {
        this.headers = Objects.requireNonNull(headers);
        return myself();
    }

    @Override
    public HttpRequest setHeaders(Map<String, String> headers) {
        this.headers = ArrayListMultiValueMap.fromMap(Objects.requireNonNull(headers));
        return myself();
    }

    private void initHeaders() {
        if(null == headers){
            headers = new ArrayListMultiValueMap<>(2);
        }
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public THIS setContentType(String contentType) {
        this.contentType = contentType;
        return myself();
    }

    @Override
    public THIS setContentType(MediaType mediaType) {
        this.contentType = mediaType.toString();
        return myself();
    }

    @Override
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public THIS setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return myself();
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public THIS setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return myself();
    }

    @Override
    public String getResultCharset() {
        return resultCharset;
    }

    @Override
    public THIS setResultCharset(String resultCharset) {
        this.resultCharset = resultCharset;
        return myself();
    }

    @Override
    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    @Override
    public THIS setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
        return myself();
    }

    @Override
    public boolean isIgnoreResponseBody() {
        return ignoreResponseBody;
    }

    @Override
    public THIS setIgnoreResponseBody(boolean ignoreResponseBody) {
        this.ignoreResponseBody = ignoreResponseBody;
        return myself();
    }

    @Override
    public boolean followRedirects() {
        return followRedirects;
    }

    @Override
    public THIS followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return myself();
    }

    @Override
    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    @Override
    public THIS setProxy(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
        return myself();
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    @Override
    public THIS setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return myself();
    }

    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }

    @Override
    public THIS setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return myself();
    }

    @Override
    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    @Override
    public THIS setX509TrustManager(X509TrustManager x509TrustManager) {
        this.x509TrustManager = x509TrustManager;
        return myself();
    }

    @Override
    public THIS addAttribute(String key, String value) {
        if(null == attributes){
            attributes = new HashMap<>(2);
        }
        attributes.put(key, value);
        return myself();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }


    @Override
    public Config getConfig() {
        return config;
    }

    /**
     * HttpRequest中的config都来自于实现类的初始化，调用此方法将系统的设置传递给HttpRequest
     * @param config config
     */
    @Override
    public void setConfig(Config config) {
        this.config = config;
    }
}
