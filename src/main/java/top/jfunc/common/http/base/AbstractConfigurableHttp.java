package top.jfunc.common.http.base;

import top.jfunc.common.http.HeaderRegular;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.Joiner;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 一些http的公共方法处理
 * @author xiongshiyan at 2018/8/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractConfigurableHttp {
    private Config config = Config.defaultConfig();

    public Config getConfig() {
        return config;
    }

    public AbstractConfigurableHttp setConfig(Config config) {
        this.config = Objects.requireNonNull(config);
        return this;
    }

    public void onBeforeIfNecessary(HttpRequest httpRequest , Method method){
        Config config = getConfig();
        if(config.hasInterceptors()){
            config.getCompositeInterceptor().onBefore(httpRequest, method);
        }
    }
    public void onAfterReturnIfNecessary(HttpRequest httpRequest , Object returnValue){
        Config config = getConfig();
        if(config.hasInterceptors()){
            config.getCompositeInterceptor().onAfterReturn(httpRequest, returnValue);
        }
    }
    public void onErrorIfNecessary(HttpRequest httpRequest , Exception exception){
        Config config = getConfig();
        if(config.hasInterceptors()){
            config.getCompositeInterceptor().onError(httpRequest, exception);
        }
    }
    public void onAfterIfNecessary(HttpRequest httpRequest){
        Config config = getConfig();
        if(config.hasInterceptors()){
            config.getCompositeInterceptor().onAfter(httpRequest);
        }
    }


    /**
     * 从CookieHandler中获取Cookies
     * @param completedUrl URL
     * @return Cookies
     * @throws IOException IOException
     */
    protected List<String> getCookies(String completedUrl , MultiValueMap<String, String> headers) throws IOException {
        if(null != getCookieHandler()){
            CookieHandler cookieHandler = getCookieHandler();
            //从源码知道CookieManager#get方法传入的Map基本没用，不为空即可，不知道这样设计干嘛的
            MultiValueMap<String, String> nonNull = null != headers ? headers : new ArrayListMultiValueMap<>(0);
            Map<String, List<String>> cookies = cookieHandler.get(URI.create(completedUrl), nonNull);
            if(null != cookies && !cookies.isEmpty()){
                return cookies.get(HeaderRegular.COOKIE.toString());
            }
        }
        return null;
    }

    /**
     * 是否支持Cookie，默认设置了CookieHandler即表示支持
     */
    protected boolean supportCookie(){
        return null != getCookieHandler();
    }

    /**
     * 如果支持Cookie，从CookieHandler中拿出来设置到Header Map中
     * @param completedUrl URL
     * @param headers 正常用户的Header Map
     * @return 处理过的Header Map
     * @throws IOException IOException
     */
    protected MultiValueMap<String, String> handleCookieIfNecessary(String completedUrl, MultiValueMap<String, String> headers) throws IOException {
        if(supportCookie()){
            List<String> cookies = getCookies(completedUrl , headers);
            if(null != cookies && !cookies.isEmpty()){
                if(null == headers){
                    headers = new ArrayListMultiValueMap<>();
                }
                headers.add(HeaderRegular.COOKIE.toString() , Joiner.on(";").join(cookies));
            }
        }
        return headers;
    }

    /**
     * 获取一个空的，防止空指针
     */
    protected InputStream emptyInputStream() {
        return new ByteArrayInputStream(new byte[]{});
    }
    /**
     * 处理路径参数、Query参数
     * @param originUrl 原始URL
     * @param routeParams 路径参数 可空
     * @param queryParams Query参数 可空
     * @param charset Query参数的字符集，null则取默认的
     * @return 处理后的URL
     */
    protected String handleUrlIfNecessary(String originUrl ,
                                          Map<String , String> routeParams ,
                                          MultiValueMap<String,String> queryParams,
                                          String charset){
        //1.处理路径参数
        String routeUrl = ParamUtil.replaceRouteParamsIfNecessary(originUrl , routeParams);

        //2.处理BaseUrl
        String urlWithBase = addBaseUrlIfNecessary(routeUrl);

        //3.合并默认的Query参数
        queryParams = ParamUtil.mergeMap(queryParams , getDefaultQueryParams());

        //3.处理Query参数
        return ParamUtil.contactUrlParams(urlWithBase, queryParams, getBodyCharsetWithDefault(charset));
    }


    protected String addBaseUrlIfNecessary(String inputUrl){
        return ParamUtil.addBaseUrlIfNecessary(getConfig().getBaseUrl() , inputUrl);
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


    public Integer getConnectionTimeoutWithDefault(Integer connectionTimeout){
        return getValueWithDefault(connectionTimeout ,
                getConfig().getDefaultConnectionTimeout());
    }

    public Integer getReadTimeoutWithDefault(Integer readTimeout){
        return getValueWithDefault(readTimeout ,
                getConfig().getDefaultReadTimeout());
    }

    public String getBodyCharsetWithDefault(String bodyCharset){
        return getValueWithDefault(bodyCharset ,
                getConfig().getDefaultBodyCharset());
    }
    public String getDefaultBodyCharset() {
        return getConfig().getDefaultBodyCharset();
    }
    public String getResultCharsetWithDefault(String resultCharset){
        return getValueWithDefault(resultCharset ,
                getConfig().getDefaultResultCharset());
    }

    public ProxyInfo getProxyInfoWithDefault(ProxyInfo proxyInfo){
        return getValueWithDefault(proxyInfo ,
                getConfig().getProxyInfo());
    }

    public HostnameVerifier getHostnameVerifier() {
        return getConfig().sslHolder().getHostnameVerifier();
    }

    public SSLContext getSSLContext() {
        return getConfig().sslHolder().getSslContext();
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return getConfig().sslHolder().getSslSocketFactory();
    }

    public X509TrustManager getX509TrustManager() {
        return getConfig().sslHolder().getX509TrustManager();
    }

    public HostnameVerifier getHostnameVerifierWithDefault(HostnameVerifier hostnameVerifier){
        return getValueWithDefault(hostnameVerifier ,
                getConfig().sslHolder().getHostnameVerifier());
    }

    public SSLContext getSSLContextWithDefault(SSLContext sslContext) {
        return getValueWithDefault(sslContext ,
                getConfig().sslHolder().getSslContext());
    }

    public SSLSocketFactory getSSLSocketFactoryWithDefault(SSLSocketFactory sslSocketFactory) {
        return getValueWithDefault(sslSocketFactory ,
                getConfig().sslHolder().getSslSocketFactory());
    }

    public X509TrustManager getX509TrustManagerWithDefault(X509TrustManager x509TrustManager){
        return getValueWithDefault(x509TrustManager ,
                getConfig().sslHolder().getX509TrustManager());
    }

    public MultiValueMap<String , String> getDefaultHeaders(){
        return getConfig().headerHolder().getHeaders();
    }

    public MultiValueMap<String , String> getDefaultQueryParams(){
        return getConfig().queryParamHolder().getParams();
    }

    /**
     * 合并默认的header
     */
    protected MultiValueMap<String , String> mergeDefaultHeaders(final MultiValueMap<String , String> headers){
        return ParamUtil.mergeMap(headers , getDefaultHeaders());
    }

    public CookieHandler getCookieHandler(){
        return getConfig().getCookieHandler();
    }
}
