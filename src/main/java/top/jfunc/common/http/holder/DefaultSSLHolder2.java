package top.jfunc.common.http.holder;

import top.jfunc.common.http.base.ssl.DefaultTrustManager2;
import top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder;
import top.jfunc.common.http.base.ssl.TrustAnyHostnameVerifier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author xiongshiyan at 2019/6/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultSSLHolder2 implements SSLHolder {
    private HostnameVerifier hostnameVerifier = new TrustAnyHostnameVerifier();
    private SSLContext sslContext = SSLSocketFactoryBuilder.create().getSSLContext();
    private SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
    private X509TrustManager x509TrustManager = new DefaultTrustManager2();

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
