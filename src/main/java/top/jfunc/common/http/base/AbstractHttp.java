package top.jfunc.common.http.base;

import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ssl.DefaultTrustManager2;
import top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder;
import top.jfunc.common.http.base.ssl.TrustAnyHostnameVerifier;
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String addBaseUrlIfNecessary(String inputUrl){
        return ParamUtil.addBaseUrlIfNecessary(baseUrl , inputUrl);
    }
}
