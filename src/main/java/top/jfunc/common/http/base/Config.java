package top.jfunc.common.http.base;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.holder.*;
import top.jfunc.common.http.interceptor.CompositeInterceptor;
import top.jfunc.common.http.interceptor.Interceptor;
import top.jfunc.common.http.request.HttpRequest;

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
    private ConfigFrozen configFrozen = new ConfigFrozen();
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

    public Config freezeConfig(){
        configFrozen.freezeConfig();
        return this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Config setBaseUrl(String baseUrl) {
        configFrozen.ensureConfigNotFreeze();
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * 统一的获取实际的值：逻辑是输入的不等于空就返回输入的，否则返回默认的
     * @param input 输入值，可能为null
     * @param defaultValue 默认值
     * @param <T> 泛型参数
     * @return 输入的值或者默认值
     */
    public <T> T getValueWithDefault(T input , T defaultValue){
        return null == input ? defaultValue : input;
    }


    public Integer getDefaultConnectionTimeout() {
        return defaultConnectionTimeout;
    }
    public Integer getConnectionTimeoutWithDefault(Integer connectionTimeout){
        return getValueWithDefault(connectionTimeout , getDefaultConnectionTimeout());
    }

    public Config setDefaultConnectionTimeout(Integer defaultConnectionTimeout) {
        configFrozen.ensureConfigNotFreeze();
        this.defaultConnectionTimeout = defaultConnectionTimeout;
        return this;
    }
    public Integer getDefaultReadTimeout() {
        return defaultReadTimeout;
    }

    public Integer getReadTimeoutWithDefault(Integer readTimeout){
        return getValueWithDefault(readTimeout , getDefaultReadTimeout());
    }

    public Config setDefaultReadTimeout(Integer defaultReadTimeout) {
        configFrozen.ensureConfigNotFreeze();
        this.defaultReadTimeout = defaultReadTimeout;
        return this;
    }

    public String getDefaultQueryCharset() {
        return queryParamHolder.getParamCharset();
    }

    public String getQueryCharsetWithDefault(String queryCharset){
        return getValueWithDefault(queryCharset , getDefaultQueryCharset());
    }

    public Config setDefaultQueryCharset(String defaultQueryCharset) {
        configFrozen.ensureConfigNotFreeze();
        this.queryParamHolder.setParamCharset(defaultQueryCharset);
        return this;
    }

    public String getDefaultBodyCharset() {
        return defaultBodyCharset;
    }

    public String getBodyCharsetWithDefault(String bodyCharset){
        return getValueWithDefault(bodyCharset , getDefaultBodyCharset());
    }
    public Config setDefaultBodyCharset(String defaultBodyCharset) {
        configFrozen.ensureConfigNotFreeze();
        this.defaultBodyCharset = defaultBodyCharset;
        return this;
    }

    public String getDefaultResultCharset() {
        return defaultResultCharset;
    }
    public String getResultCharsetWithDefault(String resultCharset){
        return getValueWithDefault(resultCharset , getDefaultResultCharset());
    }

    public Config setDefaultResultCharset(String defaultResultCharset) {
        configFrozen.ensureConfigNotFreeze();
        this.defaultResultCharset = defaultResultCharset;
        return this;
    }
    public ProxyInfo getProxyInfoWithDefault(ProxyInfo proxyInfo){
        return getValueWithDefault(proxyInfo , getProxyInfo());
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public Config setProxyInfo(ProxyInfo proxyInfo) {
        configFrozen.ensureConfigNotFreeze();
        this.proxyInfo = proxyInfo;
        return this;
    }
    public HostnameVerifier getHostnameVerifierWithDefault(HostnameVerifier hostnameVerifier){
        return getValueWithDefault(hostnameVerifier , sslHolder.getHostnameVerifier());
    }

    public SSLContext getSSLContextWithDefault(SSLContext sslContext) {
        return getValueWithDefault(sslContext , sslHolder.getSslContext());
    }

    public SSLSocketFactory getSSLSocketFactoryWithDefault(SSLSocketFactory sslSocketFactory) {
        return getValueWithDefault(sslSocketFactory , sslHolder.getSslSocketFactory());
    }

    public X509TrustManager getX509TrustManagerWithDefault(X509TrustManager x509TrustManager){
        return getValueWithDefault(x509TrustManager , sslHolder.getX509TrustManager());
    }
    public SSLHolder sslHolder(){
        return sslHolder;
    }

    public HeaderHolder headerHolder() {
        return headerHolder;
    }

    public ParamHolder queryParamHolder(){
        return queryParamHolder;
    }

    public CookieHandler getCookieHandler() {
        return cookieHandler;
    }

    public Config setCookieHandler(CookieHandler cookieHandler) {
        configFrozen.ensureConfigNotFreeze();
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
        configFrozen.ensureConfigNotFreeze();
        this.compositeInterceptor = compositeInterceptor;
        return this;
    }
    public Config addInterceptor(Interceptor interceptor , Interceptor... interceptors){
        configFrozen.ensureConfigNotFreeze();
        if(null == this.compositeInterceptor){
            this.compositeInterceptor = new CompositeInterceptor();
        }
        this.compositeInterceptor.add(interceptor , interceptors);
        return this;
    }

    void onBeforeIfNecessary(HttpRequest httpRequest , Method method){
        if(hasInterceptors()){
            compositeInterceptor.onBefore(httpRequest, method);
        }
    }

    void onAfterReturnIfNecessary(HttpRequest httpRequest , Object returnValue){
        if(hasInterceptors()){
            compositeInterceptor.onAfterReturn(httpRequest, returnValue);
        }
    }
    void onErrorIfNecessary(HttpRequest httpRequest , Exception exception){
        if(hasInterceptors()){
            compositeInterceptor.onError(httpRequest, exception);
        }
    }
    void onAfterIfNecessary(HttpRequest httpRequest){
        if(hasInterceptors()){
            compositeInterceptor.onAfter(httpRequest);
        }
    }
    private boolean hasInterceptors(){
        return null != this.compositeInterceptor
                && this.compositeInterceptor.hasInterceptors();
    }
}
