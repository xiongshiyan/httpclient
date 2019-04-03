package top.jfunc.common.http.base;

import top.jfunc.common.utils.ArrayListMultimap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.util.Objects;

/**
 * 一些http的公共方法处理
 * @author xiongshiyan at 2018/8/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractConfigurableHttp {
    private Config config = Config.defaultConfig();

    public Config getConfig() {
        return Objects.requireNonNull(config);
    }

    public AbstractConfigurableHttp setConfig(Config config) {
        this.config = Objects.requireNonNull(config);
        return this;
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

    public String getResultCharsetWithDefault(String resultCharset){
        return getConfig().getResultCharsetWithDefault(resultCharset);
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
