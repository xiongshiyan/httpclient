package top.jfunc.common.http.base;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ssl.DefaultTrustManager2;
import top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder;
import top.jfunc.common.http.base.ssl.TrustAnyHostnameVerifier;
import top.jfunc.common.http.smart.Request;
import top.jfunc.common.http.smart.SSLRequest;
import top.jfunc.common.utils.StrUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 一些http的公共方法处理
 * @author xiongshiyan at 2018/8/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractHttp {
    protected HostnameVerifier getDefaultHostnameVerifier(){
        return new TrustAnyHostnameVerifier();
    }
    protected SSLContext getDefaultSSLContext(){
        return SSLSocketFactoryBuilder.create().getSSLContext();
    }
    protected SSLSocketFactory getDefaultSSLSocketFactory(){
        SSLContext sslContext = getDefaultSSLContext();
        if(null != sslContext){
            return sslContext.getSocketFactory();
        }
        return null;
    }
    protected X509TrustManager getDefaultX509TrustManager(){
        return new DefaultTrustManager2();
    }

    protected HostnameVerifier getHostnameVerifier(Request request){
        HostnameVerifier hostnameVerifier = getHostnameVerifier();
        if(request instanceof SSLRequest){
            SSLRequest sslRequest = (SSLRequest)request;
            HostnameVerifier verifier = sslRequest.getHostnameVerifier();
            return null == verifier ? hostnameVerifier : verifier;
        }
        return hostnameVerifier;
    }

    /**
     * 子类可以复写此方法获取 HostnameVerifier ，否则默认
     */
    protected HostnameVerifier getHostnameVerifier(){
        return getDefaultHostnameVerifier();
    }

    protected SSLSocketFactory getSSLSocketFactory(Request request){
        SSLSocketFactory sslSocketFactory = getSSLSocketFactory();
        if(request instanceof SSLRequest){
            SSLRequest sslRequest = (SSLRequest)request;
            SSLSocketFactory factory = sslRequest.getSslSocketFactory();
            return null == factory ? sslSocketFactory : factory;
        }
        return sslSocketFactory;
    }
    /**
     * 子类可以复写此方法获取 SSLSocketFactory ，否则默认
     */
    protected SSLSocketFactory getSSLSocketFactory(){
        return getDefaultSSLSocketFactory();
    }

    protected SSLContext getSSLContext(Request request){
        SSLContext sslContext = getSSLContext();
        if(request instanceof SSLRequest){
            SSLRequest sslRequest = (SSLRequest)request;
            SSLContext context = sslRequest.getSslContext();
            return null == context ? sslContext : context;
        }
        return sslContext;
    }

    /**
     * 子类可以复写此方法获取 SSLContext ，否则默认
     */
    protected SSLContext getSSLContext(){
        return getDefaultSSLContext();
    }
    protected X509TrustManager getX509TrustManager(Request request){
        X509TrustManager x509TrustManager = getX509TrustManager();
        if(request instanceof SSLRequest){
            SSLRequest sslRequest = (SSLRequest)request;
            X509TrustManager manager = sslRequest.getX509TrustManager();
            return null == manager ? x509TrustManager : manager;
        }
        return x509TrustManager;
    }

    /**
     * 子类可以复写此方法获取 X509TrustManager ，否则默认
     */
    protected X509TrustManager getX509TrustManager(){
        return getDefaultX509TrustManager();
    }

    /**
     * 获取一个空的，防止空指针
     */
    protected InputStream emptyInputStream() {
        return new ByteArrayInputStream(new byte[]{});
    }

    public boolean isHttps(String url){
        return ParamUtil.isHttps(url);
    }

    /**
     * BaseUrl,如果设置了就在正常传送的URL之前添加上
     */
    private String baseUrl;
    /**
     * 连接超时时间
     */
    private Integer defaultConnectionTimeout = HttpConstants.DEFAULT_CONNECT_TIMEOUT;
    /**
     * 读数据超时时间
     */
    private Integer defaultReadTimeout = HttpConstants.DEFAULT_READ_TIMEOUT;
    /**
     * 请求体编码
     */
    private String defaultBodyCharset = HttpConstants.DEFAULT_CHARSET;
    /**
     * 返回体编码
     */
    private String defaultResultCharset = HttpConstants.DEFAULT_CHARSET;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String addBaseUrlIfNecessary(String inputUrl){
        return ParamUtil.addBaseUrlIfNecessary(baseUrl , inputUrl);
    }

    public Integer getDefaultConnectionTimeout() {
        return defaultConnectionTimeout;
    }

    public void setDefaultConnectionTimeout(Integer defaultConnectionTimeout) {
        this.defaultConnectionTimeout = defaultConnectionTimeout;
    }

    public Integer getConnectionTimeoutWithDefault(Integer connectionTimeout){
        return null == connectionTimeout ? defaultConnectionTimeout : connectionTimeout;
    }

    public Integer getDefaultReadTimeout() {
        return defaultReadTimeout;
    }

    public void setDefaultReadTimeout(Integer defaultReadTimeout) {
        this.defaultReadTimeout = defaultReadTimeout;
    }
    public Integer getReadTimeoutWithDefault(Integer readTimeout){
        return null == readTimeout ? defaultReadTimeout : readTimeout;
    }

    public String getDefaultBodyCharset() {
        return defaultBodyCharset;
    }

    public void setDefaultBodyCharset(String defaultBodyCharset) {
        this.defaultBodyCharset = defaultBodyCharset;
    }

    public String getBodyCharsetWithDefault(String bodyCharset){
        return null == bodyCharset ? defaultBodyCharset : bodyCharset;
    }

    public String getDefaultResultCharset() {
        return defaultResultCharset;
    }

    public void setDefaultResultCharset(String defaultResultCharset) {
        this.defaultResultCharset = defaultResultCharset;
    }
    public String getResultCharsetWithDefault(String resultCharset){
        return null == resultCharset ? defaultResultCharset : resultCharset;
    }
}
