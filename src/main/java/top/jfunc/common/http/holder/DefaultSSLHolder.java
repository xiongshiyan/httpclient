package top.jfunc.common.http.holder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author xiongshiyan at 2019/6/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultSSLHolder implements SSLHolder{
    /**
     * HostnameVerifier
     * @see top.jfunc.common.http.base.Config#hostnameVerifier
     */
    private HostnameVerifier hostnameVerifier = null;
    /**
     * SSLContext
     * @see top.jfunc.common.http.base.Config#sslContext
     */
    private SSLContext sslContext = null;
    /**
     * SSLSocketFactory
     * @see top.jfunc.common.http.base.Config#sslSocketFactory
     */
    private SSLSocketFactory sslSocketFactory = null;
    /**
     * X509TrustManager
     * @see top.jfunc.common.http.base.Config#x509TrustManager
     */
    private X509TrustManager x509TrustManager = null;

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    @Override
    public SSLHolder setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }

    @Override
    public SSLHolder setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    /**
     * 因为一般地 SslSocketFactory 都是从sslContext产生出来的 ， 所以如果没显式设置就从sslContext产生
     */
    @Override
    public SSLSocketFactory getSslSocketFactory() {
        if(null == sslSocketFactory && null != sslContext){
            return sslContext.getSocketFactory();
        }
        return sslSocketFactory;
    }

    @Override
    public SSLHolder setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    @Override
    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    @Override
    public SSLHolder setX509TrustManager(X509TrustManager x509TrustManager) {
        this.x509TrustManager = x509TrustManager;
        return this;
    }
}
