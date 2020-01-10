package top.jfunc.common.http.holderrequest.impl;

import top.jfunc.common.ChainCall;
import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.component.CompletedUrlCreator;
import top.jfunc.common.http.component.DefaultCompletedUrlCreator;
import top.jfunc.common.http.holder.*;
import top.jfunc.common.http.holderrequest.HolderHttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.net.URL;
import java.util.Map;

/**
 * 基本请求参数实现:可用于无请求体如Get等的请求
 * T泛型为了变种的setter返回this便于链式调用
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class BaseHolderHttpRequest<THIS extends BaseHolderHttpRequest> implements HolderHttpRequest, ChainCall<THIS> {
    /**
     * 请求的URL
     */
    private UrlHolder urlHolder = new DefaultUrlHolder();
    private String cacheCompletedUrl;
    /**
     * 完整URL处理器
     */
    private CompletedUrlCreator completedUrlCreator = new DefaultCompletedUrlCreator();
    /**
     * 请求头
     */
    private HeaderHolder headerHolder = new DefaultHeaderHolder();
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
     * SSL相关设置
     */
    private SSLHolder sslHolder = new DefaultSSLHolder();
    /**
     * 属性设置器
     */
    private AttributeHolder attributeHolder = new DefaultAttributeHolder();

    /**
     * 代理设置,如果有就设置
     * Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port));
     */
    private ProxyInfo proxyInfo = null;

    /**用于接收系统的默认设置*/
    private Config config;

    /**
     * method
     */
    private Method method;

    public BaseHolderHttpRequest(String url){this.urlHolder.setUrl(url);}
    public BaseHolderHttpRequest(URL url){this.urlHolder.setUrl(url);}
    public BaseHolderHttpRequest(){}

    @Override
    public String getCompletedUrl() {
        if(null == this.cacheCompletedUrl){
            this.cacheCompletedUrl = getCompletedUrlCreator().complete(this);
        }
        return this.cacheCompletedUrl;
    }

    @Override
    public THIS setUrl(String url) {
        this.urlHolder.setUrl(url);
        return myself();
    }
    @Override
    public THIS setUrl(URL url) {
        this.urlHolder.setUrl(url);
        return myself();
    }

    public CompletedUrlCreator getCompletedUrlCreator() {
        return completedUrlCreator;
    }

    public void setCompletedUrlCreator(CompletedUrlCreator completedUrlCreator) {
        this.completedUrlCreator = completedUrlCreator;
    }

    @Override
    public THIS addRouteParam(String key, String value) {
        routeParamHolder().put(key, value);
        return myself();
    }

    @Override
    public THIS setRouteParams(Map<String, String> routeParams) {
        routeParamHolder().setMap(routeParams);
        return myself();
    }

    @Override
    public THIS addQueryParam(String key, String value , String... values){
        queryParamHolder().addParam(key, value, values);
        return myself();
    }

    @Override
    public THIS setQueryParams(MultiValueMap<String, String> queryParams) {
        queryParamHolder().setParams(queryParams);
        return myself();
    }

    @Override
    public THIS setQueryParams(Map<String, String> queryParams) {
        queryParamHolder().setParams(queryParams);
        return myself();
    }

    @Override
    public HeaderHolder headerHolder() {
        return headerHolder;
    }

    @Override
    public THIS setHeader(String key, String value) {
        headerHolder.setHeader(key, value);
        return myself();
    }

    @Override
    public THIS addHeader(String key, String value , String... values){
        headerHolder().addHeader(key, value, values);
        return myself();
    }

    @Override
    public THIS setHeaders(Map<String, String> headers) {
        headerHolder().setHeaders(headers);
        return myself();
    }

    @Override
    public THIS setHeaders(MultiValueMap<String, String> headers) {
        headerHolder().setHeaders(headers);
        return myself();
    }

    public THIS addFormHeader(){
        return setContentType(MediaType.APPLICATIPON_FORM_DATA.withCharset(HttpConstants.DEFAULT_CHARSET));
    }
    public THIS addJsonHeader(){
        return setContentType(MediaType.APPLICATIPON_JSON.withCharset(HttpConstants.DEFAULT_CHARSET));
    }
    public THIS addXmlHeader(){
        return setContentType(MediaType.TXT_XML.withCharset(HttpConstants.DEFAULT_CHARSET));
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
    public THIS setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return myself();
    }

    @Override
    public THIS setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return myself();
    }

    @Override
    public THIS setResultCharset(String resultCharset) {
        this.resultCharset = resultCharset;
        return myself();
    }

    @Override
    public THIS setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
        return myself();
    }

    @Override
    public THIS setIgnoreResponseBody(boolean ignoreResponseBody) {
        this.ignoreResponseBody = ignoreResponseBody;
        return myself();
    }

    @Override
    public THIS followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return myself();
    }

    @Override
    public THIS setProxy(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
        return myself();
    }

    @Override
    public UrlHolder urlHolder() {
        return urlHolder;
    }

    @Override
    public THIS urlHolder(UrlHolder urlHolder) {
        this.urlHolder = urlHolder;
        return myself();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
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
    public boolean followRedirects() {
        return followRedirects;
    }

    @Override
    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    @Override
    public SSLHolder sslHolder() {
        return sslHolder;
    }

    @Override
    public AttributeHolder attributeHolder() {
        return attributeHolder;
    }

    @Override
    public THIS addAttribute(String key, String value) {
        this.attributeHolder.addAttribute(key, value);
        return myself();
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

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public THIS setMethod(Method method) {
        this.method = method;
        return myself();
    }
}
