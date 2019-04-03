package top.jfunc.common.http.base;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ssl.DefaultTrustManager2;
import top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder;
import top.jfunc.common.http.base.ssl.TrustAnyHostnameVerifier;
import top.jfunc.common.utils.ArrayListMultimap;

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
     * HostnameVerifier
     */
    private HostnameVerifier hostnameVerifier               = getDefaultHostnameVerifier();
    /**
     * SSLContext
     */
    private SSLContext sslContext                           = getDefaultSSLContext();
    /**
     * SSLSocketFactory
     */
    private SSLSocketFactory sslSocketFactory               = getDefaultSSLSocketFactory();
    /**
     * X509TrustManager
     */
    private X509TrustManager x509TrustManager               = getDefaultX509TrustManager();
    /**
     * 默认的请求头,每个请求都会加上
     */
    private ArrayListMultimap<String,String> defaultHeaders = null;

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
        return null == bodyCharset ? defaultBodyCharset : bodyCharset;
    }

    public String getDefaultResultCharset() {
        return defaultResultCharset;
    }

    public Config setDefaultResultCharset(String defaultResultCharset) {
        this.defaultResultCharset = defaultResultCharset;
        return this;
    }

    public String getResultCharsetWithDefault(String resultCharset){
        return null == resultCharset ? defaultResultCharset : resultCharset;
    }
    public Config setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public Config setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    public Config setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    public Config setX509TrustManager(X509TrustManager x509TrustManager) {
        this.x509TrustManager = x509TrustManager;
        return this;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public SSLContext getSSLContext() {
        return sslContext;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return sslSocketFactory;
    }

    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    public Config setDefaultHeaders(ArrayListMultimap<String, String> headers) {
        this.defaultHeaders = headers;
        return this;
    }
    public Config setDefaultHeaders(Map<String, String> headers) {
        if(null == defaultHeaders){
            defaultHeaders = new ArrayListMultimap<>();
        }
        headers.forEach((k,v)->this.defaultHeaders.put(k,v));
        return this;
    }
    public Config addDefaultHeader(String key, String value){
        if(null == defaultHeaders){
            defaultHeaders = new ArrayListMultimap<>();
        }
        this.defaultHeaders.put(key, value);
        return this;
    }

    public ArrayListMultimap<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }




    private HostnameVerifier getDefaultHostnameVerifier(){
        return new TrustAnyHostnameVerifier();
    }
    private X509TrustManager getDefaultX509TrustManager(){
        return new DefaultTrustManager2();
    }
    private SSLContext getDefaultSSLContext(){
        return SSLSocketFactoryBuilder.create().getSSLContext();
    }
    private SSLSocketFactory getDefaultSSLSocketFactory(){
        SSLContext sslContext = getDefaultSSLContext();
        if(null != sslContext){
            return sslContext.getSocketFactory();
        }
        return null;
    }
}
