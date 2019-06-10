package top.jfunc.common.http.base;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.holder.*;
import top.jfunc.common.http.interceptor.CompositeInterceptor;
import top.jfunc.common.http.interceptor.Interceptor;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.StrUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.net.CookieHandler;

/**
 * 全局公共配置
 * @author xiongshiyan at 2018/8/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class Config {
    /**
     * BaseUrl,如果设置了就在正常传送的URL之前添加上
     */
    private String baseUrl                                  = null;
    /**
     * 连接超时时间
     */
    private Integer defaultConnectionTimeout                = HttpConstants.DEFAULT_CONNECT_TIMEOUT;
    /**
     * 读数据超时时间
     */
    private Integer defaultReadTimeout                      = HttpConstants.DEFAULT_READ_TIMEOUT;
    /**
     * 请求体编码
     */
    private String defaultBodyCharset                       = HttpConstants.DEFAULT_CHARSET;
    /**
     * 返回体编码
     */
    private String defaultResultCharset                     = HttpConstants.DEFAULT_CHARSET;
    /**
     * 代理设置,如果有就设置
     * Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port));
     */
    private ProxyInfo proxyInfo = null;
    /**
     * SSL相关设置处理器
     */
    private SSLHolder sslHolder = new DefaultSSLHolder2();
    /**
     * 默认的请求头,每个请求都会加上//private MultiValueMap<String,String> defaultHeaders = null;
     */
    private HeaderHolder headerHolder = new DefaultHeaderHolder();
    /**
     * 默认的请求Query参数,每个请求都会加上//private MultiValueMap<String,String> defaultQueryParams = null;
     */
    private ParamHolder queryParamHolder = new DefaultParamHolder();
    /**
     * CookieHandler，只要设置了就表示支持Cookie
     */
    private CookieHandler cookieHandler = null;
    /**
     * 拦截器链
     */
    private CompositeInterceptor compositeInterceptor;

    public static Config defaultConfig(){
        return new Config();
    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public Config setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public String addBaseUrlIfNecessary(String inputUrl){
        return ParamUtil.addBaseUrlIfNecessary(getBaseUrl() , inputUrl);
    }

    public Integer getDefaultConnectionTimeout() {
        return defaultConnectionTimeout;
    }

    public Config setDefaultConnectionTimeout(Integer defaultConnectionTimeout) {
        this.defaultConnectionTimeout = defaultConnectionTimeout;
        return this;
    }

    public Integer getConnectionTimeoutWithDefault(Integer connectionTimeout){
        return null == connectionTimeout ? defaultConnectionTimeout : connectionTimeout;
    }

    public Integer getDefaultReadTimeout() {
        return defaultReadTimeout;
    }

    public Config setDefaultReadTimeout(Integer defaultReadTimeout) {
        this.defaultReadTimeout = defaultReadTimeout;
        return this;
    }

    public Integer getReadTimeoutWithDefault(Integer readTimeout){
        return null == readTimeout ? defaultReadTimeout : readTimeout;
    }
    public String getDefaultBodyCharset() {
        return defaultBodyCharset;
    }

    public Config setDefaultBodyCharset(String defaultBodyCharset) {
        this.defaultBodyCharset = defaultBodyCharset;
        return this;
    }

    public String getBodyCharsetWithDefault(String bodyCharset){
        return StrUtil.isEmpty(bodyCharset) ? defaultBodyCharset : bodyCharset;
    }

    public String getDefaultResultCharset() {
        return defaultResultCharset;
    }

    public Config setDefaultResultCharset(String defaultResultCharset) {
        this.defaultResultCharset = defaultResultCharset;
        return this;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public Config setProxyInfo(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
        return this;
    }

    public ProxyInfo getProxyInfoWithDefault(ProxyInfo proxyInfo){
        return null == proxyInfo ? this.proxyInfo : proxyInfo;
    }

    public String getResultCharsetWithDefault(String resultCharset){
        return StrUtil.isEmpty(resultCharset) ? defaultResultCharset : resultCharset;
    }

    public SSLHolder sslHolder(){
        return sslHolder;
    }

    public HostnameVerifier getHostnameVerifier() {
        return sslHolder.getHostnameVerifier();
    }

    public HostnameVerifier getHostnameVerifierWithDefault(HostnameVerifier hostnameVerifier){
        return null == hostnameVerifier ? getHostnameVerifier() : hostnameVerifier;
    }

    public SSLContext getSSLContext() {
        return sslHolder.getSslContext();
    }

    public SSLContext getSSLContextWithDefault(SSLContext sslContext) {
        return null == sslContext ? getSSLContext() : sslContext;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return sslHolder.getSslSocketFactory();
    }

    public SSLSocketFactory getSSLSocketFactoryWithDefault(SSLSocketFactory sslSocketFactory) {
        return null == sslSocketFactory ? getSSLSocketFactory() : sslSocketFactory;
    }

    public X509TrustManager getX509TrustManager() {
        return sslHolder.getX509TrustManager();
    }

    public X509TrustManager getX509TrustManagerWithDefault(X509TrustManager x509TrustManager){
        return null == x509TrustManager ? getX509TrustManager() : x509TrustManager;
    }


    public MultiValueMap<String, String> getDefaultHeaders() {
        return headerHolder.getHeaders();
    }

    public HeaderHolder headerHolder() {
        return headerHolder;
    }

    public MultiValueMap<String, String> getDefaultQueryParams() {
        return queryParamHolder.getParams();
    }

    public ParamHolder queryParamHolder(){
        return queryParamHolder;
    }

    public CookieHandler getCookieHandler() {
        return cookieHandler;
    }

    public Config setCookieHandler(CookieHandler cookieHandler) {
        this.cookieHandler = cookieHandler;
        if(null == CookieHandler.getDefault()){
            CookieHandler.setDefault(cookieHandler);
        }
        return this;
    }

    public CompositeInterceptor getCompositeInterceptor() {
        return compositeInterceptor;
    }

    public Config setCompositeInterceptor(CompositeInterceptor compositeInterceptor) {
        this.compositeInterceptor = compositeInterceptor;
        return this;
    }
    public Config addInterceptor(Interceptor interceptor , Interceptor... interceptors){
        if(null == this.compositeInterceptor){
            this.compositeInterceptor = new CompositeInterceptor();
        }
        this.compositeInterceptor.add(interceptor , interceptors);
        return this;
    }

    public boolean hasInterceptors(){
        return null != this.compositeInterceptor
                && this.compositeInterceptor.hasInterceptors();
    }
}
