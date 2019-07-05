package top.jfunc.common.http.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.common.http.HeaderRegular;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.*;

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
    private static final Logger logger = LoggerFactory.getLogger(AbstractConfigurableHttp.class);

    static {
        logger.info("you can instantiate an implementation class of SmartHttpClient interface AND Do more setting like this:");
        logger.info("\n      smartHttpClient.setConfig(Config.defaultConfig()\n" +
                    "        .setBaseUrl(\"https://fanyi.baidu.com/\")\n" +
                    "        .addDefaultHeader(\"xx\" , \"xx\")\n" +
                    "        .setDefaultBodyCharset(\"UTF-8\")\n" +
                    "        .setDefaultResultCharset(\"UTF-8\")\n" +
                    "        .setDefaultConnectionTimeout(15000)\n" +
                    "        .setDefaultReadTimeout(15000))...");
    }


    private ConfigFrozen configFrozen = new ConfigFrozen();

    private Config config = Config.defaultConfig();

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        configFrozen.ensureConfigNotFreeze();
        this.config = Objects.requireNonNull(config);
    }

    public void freezeConfig() {
        //本身冻结
        configFrozen.freezeConfig();
        //Config冻结
        config.freezeConfig();
    }

    protected void onBeforeIfNecessary(HttpRequest httpRequest , Method method){
        config.onBeforeIfNecessary(httpRequest, method);
    }
    protected void onAfterReturnIfNecessary(HttpRequest httpRequest , Object returnValue){
        config.onAfterReturnIfNecessary(httpRequest, returnValue);
    }
    protected void onErrorIfNecessary(HttpRequest httpRequest , Exception exception){
        config.onErrorIfNecessary(httpRequest, exception);
    }
    protected void onAfterIfNecessary(HttpRequest httpRequest){
        config.onAfterIfNecessary(httpRequest);
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
        queryParams = MapUtil.mergeMap(queryParams , getDefaultQueryParams());

        //4.处理Query参数
        return ParamUtil.contactUrlParams(urlWithBase, queryParams, getQueryCharsetWithDefault(charset));
    }

    /**
     * 处理基路径和默认Query参数：注意url可能已经带Query参数了
     * @param originUrl 客户设置的url
     * @return 处理后的url
     */
    protected String handleUrlIfNecessary(String originUrl){
        String urlWithBase = addBaseUrlIfNecessary(originUrl);
        return ParamUtil.contactUrlParams(urlWithBase, getDefaultQueryParams(), getDefaultQueryCharset());
    }


    protected String addBaseUrlIfNecessary(String inputUrl){
        return ParamUtil.concatUrlIfNecessary(getConfig().getBaseUrl() , inputUrl);
    }

    protected Integer getConnectionTimeoutWithDefault(Integer connectionTimeout){
        return config.getConnectionTimeoutWithDefault(connectionTimeout);
    }

    protected Integer getReadTimeoutWithDefault(Integer readTimeout){
        return config.getReadTimeoutWithDefault(readTimeout);
    }

    protected String getQueryCharsetWithDefault(String queryCharset){
        return config.getQueryCharsetWithDefault(queryCharset);
    }
    protected String getDefaultQueryCharset() {
        return config.getDefaultQueryCharset();
    }
    protected String getDefaultBodyCharset() {
        return config.getDefaultBodyCharset();
    }

    /**
     * bodyCharset->contentType->全局默认
     */
    protected String calculateBodyCharset(String bodyCharset , String contentType){
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

    protected String getResultCharsetWithDefault(String resultCharset){
        return config.getResultCharsetWithDefault(resultCharset);
    }

    protected ProxyInfo getProxyInfoWithDefault(ProxyInfo proxyInfo){
        return config.getProxyInfoWithDefault(proxyInfo);
    }

    protected HostnameVerifier getHostnameVerifier() {
        return config.sslHolder().getHostnameVerifier();
    }

    protected SSLContext getSSLContext() {
        return config.sslHolder().getSslContext();
    }

    protected SSLSocketFactory getSSLSocketFactory() {
        return config.sslHolder().getSslSocketFactory();
    }

    protected X509TrustManager getX509TrustManager() {
        return config.sslHolder().getX509TrustManager();
    }

    protected HostnameVerifier getHostnameVerifierWithDefault(HostnameVerifier hostnameVerifier){
        return config.getHostnameVerifierWithDefault(hostnameVerifier);
    }

    protected SSLContext getSSLContextWithDefault(SSLContext sslContext) {
        return config.getSSLContextWithDefault(sslContext);
    }

    protected SSLSocketFactory getSSLSocketFactoryWithDefault(SSLSocketFactory sslSocketFactory) {
        return config.getSSLSocketFactoryWithDefault(sslSocketFactory);
    }

    protected X509TrustManager getX509TrustManagerWithDefault(X509TrustManager x509TrustManager){
        return config.getX509TrustManagerWithDefault(x509TrustManager);
    }

    protected MultiValueMap<String , String> getDefaultHeaders(){
        return config.headerHolder().getHeaders();
    }

    protected MultiValueMap<String , String> getDefaultQueryParams(){
        return config.queryParamHolder().getParams();
    }

    /**
     * 合并默认的header
     */
    protected MultiValueMap<String , String> mergeDefaultHeaders(final MultiValueMap<String , String> headers){
        return MapUtil.mergeMap(headers , getDefaultHeaders());
    }

    protected CookieHandler getCookieHandler(){
        return config.getCookieHandler();
    }
}
