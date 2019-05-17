package top.jfunc.common.http.base;

import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.utils.ArrayListMultimap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
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
                                          ArrayListMultimap<String,String> queryParams,
                                          String charset){
        //1.处理路径参数
        String routeUrl = ParamUtil.replaceRouteParamsIfNecessary(originUrl , routeParams);

        //2.处理BaseUrl
        String urlWithBase = addBaseUrlIfNecessary(routeUrl);

        //3.处理Query参数
        return ParamUtil.contactUrlParams(urlWithBase, queryParams, getBodyCharsetWithDefault(charset));
    }

    /////////////////////////////////////以下方法都由config代理，只是为了调用方便//////////////////////////////

    protected String addBaseUrlIfNecessary(String inputUrl){
        return getConfig().addBaseUrlIfNecessary(inputUrl);
    }

    public Integer getConnectionTimeoutWithDefault(Integer connectionTimeout){
        return getConfig().getConnectionTimeoutWithDefault(connectionTimeout);
    }

    public Integer getReadTimeoutWithDefault(Integer readTimeout){
        return getConfig().getReadTimeoutWithDefault(readTimeout);
    }

    public String getBodyCharsetWithDefault(String bodyCharset){
        return getConfig().getBodyCharsetWithDefault(bodyCharset);
    }
    public String getDefaultBodyCharset() {
        return getConfig().getDefaultBodyCharset();
    }
    public String getResultCharsetWithDefault(String resultCharset){
        return getConfig().getResultCharsetWithDefault(resultCharset);
    }
    public String getDefaultResultCharset() {
        return getConfig().getDefaultResultCharset();
    }
    public HostnameVerifier getHostnameVerifier() {
        return getConfig().getHostnameVerifier();
    }

    public SSLContext getSSLContext() {
        return getConfig().getSSLContext();
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return getConfig().getSSLSocketFactory();
    }

    public X509TrustManager getX509TrustManager() {
        return getConfig().getX509TrustManager();
    }

    public ArrayListMultimap<String , String> getDefaultHeaders(){
        return getConfig().getDefaultHeaders();
    }

    /**
     * 合并默认的header
     */
    protected ArrayListMultimap<String , String> mergeDefaultHeaders(final ArrayListMultimap<String , String> headers){
        ArrayListMultimap<String, String> defaultHeaders = getDefaultHeaders();
        if(null == headers){
            return defaultHeaders;
        }
        if(null == defaultHeaders){
            return headers;
        }

        //合并两个
        for (String key : defaultHeaders.keySet()) {
            defaultHeaders.get(key).forEach((v)-> headers.put(key , v));
        }

        return headers;
    }
}
