package top.jfunc.common.http.holder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 处置SSL相关设置的
 * @author xiongshiyan
 */
public interface SSLHolder {
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
     * @param sslContext SSLContext
     * @return this
     */
    SSLHolder setSslContext(SSLContext sslContext);

    /**
     * SSLSocketFactory
     * @return SSLSocketFactory
     */
    SSLSocketFactory getSslSocketFactory();

    /**
     * 设置SSLSocketFactory
     * @param sslSocketFactory SSLSocketFactory
     * @return this
     */
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
