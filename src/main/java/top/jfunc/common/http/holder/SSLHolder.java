package top.jfunc.common.http.holder;

import top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;

/**
 * 处置SSL相关设置的
 * @author xiongshiyan
 */
public interface SSLHolder{
    /**
     * HostnameVerifier
     * @return HostnameVerifier
     */
    HostnameVerifier getHostnameVerifier();

    /**
     * 设置HostnameVerifier
     * @param hostnameVerifier HostnameVerifier
     * @return this
     */
    SSLHolder setHostnameVerifier(HostnameVerifier hostnameVerifier);

    /**
     * SSLContext
     * @return SSLContext
     */
    SSLContext getSslContext();

    /**
     * 设置 SSLContext
     * @see SSLSocketFactoryBuilder#getSSLContext()
     * @see SSLSocketFactoryBuilder#getSSLContext(String, String)
     * @see SSLSocketFactoryBuilder#getSSLContext(InputStream, String)
     * @param sslContext SSLContext
     * @return this
     */
    SSLHolder setSslContext(SSLContext sslContext);

    /**
     * SSLSocketFactory
     * @see SSLHolder#getSslContext()
     * @return SSLSocketFactory
     */
    SSLSocketFactory getSslSocketFactory();

    /**
     * 设置SSLSocketFactory
     * 废弃此方法,调用{@link SSLHolder#setSslContext(SSLContext)}设置
     * @see SSLHolder#setSslContext(SSLContext)
     * @param sslSocketFactory SSLSocketFactory
     * @return this
     */
    @Deprecated
    SSLHolder setSslSocketFactory(SSLSocketFactory sslSocketFactory);

    /**
     * X509TrustManager
     * @return X509TrustManager
     */
    X509TrustManager getX509TrustManager();

    /**
     * 设置X509TrustManager
     * @param x509TrustManager X509TrustManager
     * @return this
     */
    SSLHolder setX509TrustManager(X509TrustManager x509TrustManager);

}
