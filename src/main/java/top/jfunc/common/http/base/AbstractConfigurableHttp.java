package top.jfunc.common.http.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.common.http.*;
import top.jfunc.common.http.cookie.CookieJar;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.util.List;
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

    protected HttpRequest onBeforeIfNecessary(HttpRequest httpRequest , Method method){
        return config.onBeforeIfNecessary(httpRequest, method);
    }
    protected void onBeforeReturnIfNecessary(HttpRequest httpRequest , Object returnValue){
        config.onBeforeReturnIfNecessary(httpRequest, returnValue);
    }
    protected void onErrorIfNecessary(HttpRequest httpRequest , Exception exception){
        config.onErrorIfNecessary(httpRequest, exception);
    }
    protected void onFinallyIfNecessary(HttpRequest httpRequest){
        config.onFinallyIfNecessary(httpRequest);
    }


    /**
     * 是否支持Cookie，默认设置了CookieJar即表示支持
     */
    protected boolean supportCookie(){
        return null != getCookieJar();
    }

    /**
     * 从CookieHandler中获取Cookies
     * @param completedUrl URL
     * @return Cookies
     * @throws IOException IOException
     */
    protected List<String> getCookies(String completedUrl , MultiValueMap<String, String> headers) throws IOException {
        CookieJar cookieJar = getCookieJar();
        if(null == cookieJar){
            return null;
        }
        return cookieJar.loadForRequest(completedUrl , headers);
    }

    /**
     * 保存Cookie
     * @param completedUrl completedUrl
     * @param responseHeaders 响应的headers
     * @throws IOException IOException
     */
    protected void saveCookies(String completedUrl, MultiValueMap<String, String> responseHeaders) throws IOException {
        CookieJar cookieJar = getCookieJar();
        if(null == cookieJar){
            return;
        }

        cookieJar.saveFromResponse(completedUrl , responseHeaders);
    }

    /**
     * 如果支持Cookie，从CookieHandler中拿出来设置到Header Map中
     * @param completedUrl URL
     * @param requestHeaders 正常用户的Header Map
     * @return 处理过的Header Map
     * @throws IOException IOException
     */
    protected MultiValueMap<String, String> addCookieIfNecessary(String completedUrl, MultiValueMap<String, String> requestHeaders) throws IOException {
        if(!supportCookie()){
            return requestHeaders;
        }

        List<String> cookies = getCookies(completedUrl , requestHeaders);
        if(CollectionUtil.isEmpty(cookies)){
            return requestHeaders;
        }

        if(null == requestHeaders){
            requestHeaders = new ArrayListMultiValueMap<>();
        }
        requestHeaders.add(HeaderRegular.COOKIE.toString() , Joiner.on(HttpConstants.SEMICOLON).join(cookies));

        return requestHeaders;
    }

    /**
     * 处理cookie相关的
     * @param completedUrl completedUrl
     * @param responseHeaders 响应的headers
     * @throws IOException IOException
     */
    protected void saveCookieIfNecessary(String completedUrl, MultiValueMap<String, String> responseHeaders) throws IOException {
        //存入Cookie
        if(!supportCookie()){
            return;
        }

        saveCookies(completedUrl, responseHeaders);
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

    protected CookieJar getCookieJar(){
        return config.getCookieJar();
    }
}
