package top.jfunc.common.http.request.impl;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.kv.Header;
import top.jfunc.common.http.kv.Parameter;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.ArrayListMultimap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 基本请求参数实现:可用于无请求体如Get等的请求
 * T泛型为了变种的setter返回this便于链式调用
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class BaseRequest<T extends BaseRequest> implements HttpRequest {
    /**
     * 请求的URL
     */
    private String url;
    /**
     * 路径参数，形如这种URL http://httpbin.org/book/{id}，保存id和id的值
     * @since 1.0.4
     */
    private Map<String , String> routeParams;
    /**
     * 查询参数，拼装在URL后面 ?
     * @since 1.0.4
     */
    private ArrayListMultimap<String,String> queryParams;
    /**
     * 请求头
     */
    private ArrayListMultimap<String,String> headers;
    /**
     * 资源类型
     */
    private String contentType = null;
    /**
     * 连接超时时间，不设置就使用系统默认的
     * @see top.jfunc.common.http.base.Config#defaultConnectionTimeout
     */
    private Integer connectionTimeout = null;
    /**
     * 读数据超时时间，不设置就使用系统默认的
     * @see top.jfunc.common.http.base.Config#defaultReadTimeout
     */
    private Integer readTimeout = null;
    /**
     * 请求体编码，不设置就使用系统默认的
     * @see top.jfunc.common.http.base.Config#defaultBodyCharset
     */
    private String bodyCharset = HttpConstants.DEFAULT_CHARSET;
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
    private boolean redirectable = !REDIRECTABLE;

    /**
     * 代理设置,如果有就设置
     * Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port));
     */
    private ProxyInfo proxyInfo = null;

    /**
     * HostnameVerifier
     * @see top.jfunc.common.http.base.Config#hostnameVerifier
     */
    private HostnameVerifier hostnameVerifier = null;
    /**
     * SSLContext
     * @see top.jfunc.common.http.base.Config#sslContext
     */
    private SSLContext sslContext = null;
    /**
     * SSLSocketFactory
     * @see top.jfunc.common.http.base.Config#sslSocketFactory
     */
    private SSLSocketFactory sslSocketFactory = null;
    /**
     * X509TrustManager
     * @see top.jfunc.common.http.base.Config#x509TrustManager
     */
    private X509TrustManager x509TrustManager = null;

    public BaseRequest(String url){this.url = url;}

    public BaseRequest(){}

    /**
     * 返回自己，便于链式调用
     * @return this
     */
    protected T myself(){
        return (T)this;
    }

    /**************************变种的Setter*******************************/
    public T setUrl(String url) {
        this.url = url;
        return myself();
    }

    public T setRouteParams(Map<String, String> routeParams) {
        this.routeParams = Objects.requireNonNull(routeParams);
        return myself();
    }

    public T addRouteParam(String key , String value){
        if(null == this.routeParams){
            this.routeParams = new HashMap<>(2);
        }
        this.routeParams.put(key, value);
        return myself();
    }

    public T setQueryParams(ArrayListMultimap<String, String> queryParams) {
        this.queryParams = Objects.requireNonNull(queryParams);
        return myself();
    }
    public T setQueryParams(Map<String, String> queryParams) {
        initQueryParams();
        queryParams.forEach((k,v)->this.queryParams.put(k,v));
        return myself();
    }
    public T addQueryParam(String key, String value){
        initQueryParams();
        this.queryParams.put(key, value);
        return myself();
    }
    public T addQueryParam(String key, String... values){
        initQueryParams();
        for (String value : values) {
            this.queryParams.put(key , value);
        }
        return myself();
    }
    public T addQueryParam(String key, Iterable<String> values){
        initQueryParams();
        for (String value : values) {
            this.queryParams.put(key , value);
        }
        return myself();
    }
    public T addQueryParam(Parameter... parameters){
        for (Parameter parameter : parameters) {
            addQueryParam(parameter.getKey() , parameter.getValue());
        }
        return myself();
    }
    public T addQueryParam(Map.Entry<String , Iterable<String>>... parameters){
        for (Map.Entry<String , Iterable<String>> parameter : parameters) {
            addQueryParam(parameter.getKey() , parameter.getValue());
        }
        return myself();
    }
    private void initQueryParams(){
        if(null == this.queryParams){
            this.queryParams = new ArrayListMultimap<>(2);
        }
    }
    public T setHeaders(Map<String, String> headers) {
        initHeaders();
        headers.forEach((k,v)->this.headers.put(k,v));
        return myself();
    }
    public T addHeader(String key, String value){
        initHeaders();
        this.headers.put(key, value);
        return myself();
    }
    public T addHeader(String key, String... values){
        initHeaders();
        for (String value : values) {
            this.headers.put(key , value);
        }
        return myself();
    }
    public T addHeader(String key, Iterable<String> values){
        initHeaders();
        for (String value : values) {
            this.headers.put(key , value);
        }
        return myself();
    }
    public T addHeader(Header... headers){
        for (Header header : headers) {
            addHeader(header.getKey() , header.getValue());
        }
        return myself();
    }
    private void initHeaders(){
        if(null == this.headers){
            this.headers = new ArrayListMultimap<>(2);
        }
    }
    public T addFormHeader(){
        return setContentType(HttpConstants.FORM_URLENCODED_WITH_DEFAULT_CHARSET);
    }
    public T addJsonHeader(){
        return setContentType(HttpConstants.JSON_WITH_DEFAULT_CHARSET);
    }
    public T addXmlHeader(){
        return setContentType(HttpConstants.TEXT_XML_WITH_DEFAULT_CHARSET);
    }

    public T setContentType(String contentType) {
        this.contentType = contentType;
        return myself();
    }

    public T setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return myself();
    }

    public T setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return myself();
    }

    public T setBodyCharset(String bodyCharset) {
        this.bodyCharset = bodyCharset;
        return myself();
    }
    public T setResultCharset(String resultCharset) {
        this.resultCharset = resultCharset;
        return myself();
    }

    public T setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
        return myself();
    }

    public T setIgnoreResponseBody(boolean ignoreResponseBody) {
        this.ignoreResponseBody = ignoreResponseBody;
        return myself();
    }

    public T setRedirectable(boolean redirectable) {
        this.redirectable = redirectable;
        //要支持重定向必须header
        if(redirectable){
            this.includeHeaders = true;
        }
        return myself();
    }

    public T setProxy(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
        return myself();
    }

    public T setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return myself();
    }

    public T setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return myself();
    }

    public T setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return myself();
    }

    public T setX509TrustManager(X509TrustManager x509TrustManager) {
        this.x509TrustManager = x509TrustManager;
        return myself();
    }

    /****************************Getter**************************/
    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Map<String, String> getRouteParams() {
        return routeParams;
    }

    @Override
    public ArrayListMultimap<String, String> getQueryParams() {
        return queryParams;
    }

    @Override
    public ArrayListMultimap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public BaseRequest setHeaders(ArrayListMultimap<String, String> headers) {
        this.headers = Objects.requireNonNull(headers);
        return myself();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public Integer getReadTimeout() {
        return readTimeout;
    }

    @Override
    public String getBodyCharset() {
        return bodyCharset;
    }

    @Override
    public String getResultCharset() {
        return resultCharset;
    }

    @Override
    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    @Override
    public boolean isIgnoreResponseBody() {
        return ignoreResponseBody;
    }

    @Override
    public boolean isRedirectable() {
        return redirectable;
    }

    @Override
    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }


    @Override
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    /**
     * 因为一般地 SslSocketFactory 都是从sslContext产生出来的 ， 所以如果没显式设置就从sslContext产生
     */
    @Override
    public SSLSocketFactory getSslSocketFactory() {
        if(null == sslSocketFactory && null != sslContext){
            return sslContext.getSocketFactory();
        }
        return sslSocketFactory;
    }

    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }

    @Override
    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }
}
