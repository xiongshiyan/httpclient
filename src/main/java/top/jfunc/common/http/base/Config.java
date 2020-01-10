package top.jfunc.common.http.base;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.cookie.CookieJar;
import top.jfunc.common.http.holder.*;
import top.jfunc.common.http.interceptor.CompositeInterceptor;
import top.jfunc.common.http.interceptor.Interceptor;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.StrUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.util.Map;

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
    private int defaultConnectionTimeout                    = HttpConstants.DEFAULT_CONNECT_TIMEOUT;
    /**
     * 读数据超时时间
     */
    private int defaultReadTimeout                          = HttpConstants.DEFAULT_READ_TIMEOUT;
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
     * CookieJar，处理Cookie
     */
    private CookieJar cookieJar = null;

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

    public int getDefaultConnectionTimeout() {
        return defaultConnectionTimeout;
    }
    public int getConnectionTimeoutWithDefault(int connectionTimeout){
        return HttpConstants.TIMEOUT_UNSIGNED == connectionTimeout ? getDefaultConnectionTimeout() : connectionTimeout;
    }
    public Config setDefaultConnectionTimeout(int defaultConnectionTimeout) {
        configFrozen.ensureConfigNotFreeze();
        this.defaultConnectionTimeout = defaultConnectionTimeout;
        return this;
    }

    public int getDefaultReadTimeout() {
        return defaultReadTimeout;
    }
    public int getReadTimeoutWithDefault(int readTimeout){
        return HttpConstants.TIMEOUT_UNSIGNED == readTimeout ? getDefaultReadTimeout() : readTimeout;
    }
    public Config setDefaultReadTimeout(int defaultReadTimeout) {
        configFrozen.ensureConfigNotFreeze();
        this.defaultReadTimeout = defaultReadTimeout;
        return this;
    }

    public String getDefaultQueryCharset() {
        return queryParamHolder.getParamCharset();
    }

    public String getQueryCharsetWithDefault(String queryCharset){
        return StrUtil.isNotEmpty(queryCharset) ? queryCharset : getDefaultQueryCharset();
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
        return StrUtil.isNotEmpty(bodyCharset) ? bodyCharset : getDefaultBodyCharset();
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
        return StrUtil.isNotEmpty(resultCharset) ? resultCharset : getDefaultResultCharset();
    }

    public Config setDefaultResultCharset(String defaultResultCharset) {
        configFrozen.ensureConfigNotFreeze();
        this.defaultResultCharset = defaultResultCharset;
        return this;
    }
    public ProxyInfo getProxyInfoWithDefault(ProxyInfo proxyInfo){
        return null != proxyInfo ? proxyInfo : getDefaultProxyInfo();
    }

    public ProxyInfo getDefaultProxyInfo() {
        return proxyInfo;
    }

    public Config setProxyInfo(ProxyInfo proxyInfo) {
        configFrozen.ensureConfigNotFreeze();
        this.proxyInfo = proxyInfo;
        return this;
    }
    public HostnameVerifier getHostnameVerifierWithDefault(HostnameVerifier hostnameVerifier){
        return null != hostnameVerifier ? hostnameVerifier : sslHolder.getHostnameVerifier();
    }

    public SSLContext getSSLContextWithDefault(SSLContext sslContext) {
        return null != sslContext ? sslContext : sslHolder.getSslContext();
    }

    public SSLSocketFactory getSSLSocketFactoryWithDefault(SSLSocketFactory sslSocketFactory) {
        return null != sslSocketFactory ? sslSocketFactory : sslHolder.getSslSocketFactory();
    }

    public X509TrustManager getX509TrustManagerWithDefault(X509TrustManager x509TrustManager){
        return null != x509TrustManager ? x509TrustManager : sslHolder.getX509TrustManager();
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

    public CookieJar getCookieJar() {
        return cookieJar;
    }

    public Config setCookieJar(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
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

    public HttpRequest onBeforeIfNecessary(HttpRequest httpRequest){
        if(hasInterceptors()){
            return compositeInterceptor.onBefore(httpRequest);
        }
        return httpRequest;
    }

    public void onBeforeReturnIfNecessary(HttpRequest httpRequest , Object returnValue){
        if(hasInterceptors()){
            compositeInterceptor.onBeforeReturn(httpRequest, returnValue);
        }
    }
    public void onErrorIfNecessary(HttpRequest httpRequest , Exception exception){
        if(hasInterceptors()){
            compositeInterceptor.onError(httpRequest, exception);
        }
    }
    public void onFinallyIfNecessary(HttpRequest httpRequest){
        if(hasInterceptors()){
            compositeInterceptor.onFinally(httpRequest);
        }
    }
    private boolean hasInterceptors(){
        return null != this.compositeInterceptor
                && this.compositeInterceptor.hasInterceptors();
    }


    /**
     * clone一份，防止全局设置被无意修改
     */
    public MultiValueMap<String , String> getDefaultQueryParams(){
        MultiValueMap<String, String> params = queryParamHolder().getParams();
        if(null == params){
            return null;
        }
        //clone一份，防止全局设置被修改
        final ArrayListMultiValueMap<String, String> temp = new ArrayListMultiValueMap<>(params.size());
        params.forEachKeyValue(temp::add);
        return temp;
    }
    /**
     * clone一份，防止全局设置被无意修改
     */
    public MultiValueMap<String , String> getDefaultHeaders(){
        MultiValueMap<String, String> headers = headerHolder().getHeaders();
        if(null == headers){
            return null;
        }
        final ArrayListMultiValueMap<String, String> temp = new ArrayListMultiValueMap<>(headers.size());
        headers.forEachKeyValue(temp::add);
        return temp;
    }

    public MultiValueMap<String , String> mergeDefaultHeaders(final MultiValueMap<String , String> headers){
        return MapUtil.mergeMap(headers , getDefaultHeaders());
    }

    /**
     * 处理Route参数、BaseURL、Query参数
     * @param originUrl 原始的URL
     * @param routeParams 路径参数
     * @param queryParams 查询参数
     * @param queryParamCharset 查询参数编码
     * @return 处理过后的URL
     */
    public String handleUrlIfNecessary(String originUrl ,
                                          Map<String, String> routeParams ,
                                          MultiValueMap<String, String> queryParams ,
                                          String queryParamCharset){
        //1.处理Route参数
        String routeUrl = ParamUtil.replaceRouteParamsIfNecessary(originUrl , routeParams);
        //2.处理BaseUrl
        String urlWithBase = ParamUtil.concatUrlIfNecessary(getBaseUrl() , routeUrl);
        //3.处理Query参数
        MultiValueMap<String, String> params = MapUtil.mergeMap(queryParams, getDefaultQueryParams());
        String queryCharsetWithDefault = getQueryCharsetWithDefault(queryParamCharset);
        return ParamUtil.contactUrlParams(urlWithBase, params, queryCharsetWithDefault);
    }
    /**
     * bodyCharset[StringHttpRequest中显式地设置为null]->contentType->全局默认
     */
    public String calculateBodyCharset(String bodyCharset , String contentType){
        //本身是可以的
        if(StrUtil.isNotEmpty(bodyCharset)){
            return bodyCharset;
        }
        if(StrUtil.isEmpty(contentType)){
            return getDefaultBodyCharset();
        }
        MediaType mediaType = MediaType.parse(contentType);
        //content-type不正确或者没带字符编码
        if(null == mediaType || null == mediaType.charset()){
            return getDefaultBodyCharset();
        }

        return mediaType.charset().name();
    }
}
