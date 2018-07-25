package cn.zytx.common.http.smart;

import cn.zytx.common.http.base.ssl.DefaultTrustManager2;
import cn.zytx.common.http.base.ssl.SSLSocketFactoryBuilder;
import cn.zytx.common.http.base.ssl.TrustAnyHostnameVerifier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author xiongshiyan at 2017/12/9
 * ssl的一些配置,使用{@link SSLSocketFactoryBuilder} 来生产 HostnameVerifier、SSLSocketFactory、SSLContext、X509TrustManager
 * @see SSLSocketFactoryBuilder
 */
public class SSLRequest extends Request{
    private HostnameVerifier hostnameVerifier = new TrustAnyHostnameVerifier();
    private SSLSocketFactory sslSocketFactory = SSLSocketFactoryBuilder.create().build();
    private SSLContext sslContext = SSLSocketFactoryBuilder.create().getSSLContext();
    private X509TrustManager x509TrustManager = new DefaultTrustManager2();

    public SSLRequest(String url){
        super(url);
    }
    public static SSLRequest of(String url){
        return new SSLRequest(url);
    }
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public SSLRequest setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public SSLRequest setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public SSLRequest setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    public SSLRequest setX509TrustManager(X509TrustManager x509TrustManager) {
        this.x509TrustManager = x509TrustManager;
        return this;
    }
}
