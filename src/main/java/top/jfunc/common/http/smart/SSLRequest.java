package top.jfunc.common.http.smart;

import top.jfunc.common.http.base.ssl.DefaultTrustManager2;
import top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder;
import top.jfunc.common.http.base.ssl.TrustAnyHostnameVerifier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 在{@link Request}的基础上添加一些ssl的一些配置,一般只在需要双向校验的地方才需要。
 * 使用{@link SSLSocketFactoryBuilder} 来生产 HostnameVerifier、SSLSocketFactory、SSLContext、X509TrustManager
 * @see SSLSocketFactoryBuilder
 * @author xiongshiyan at 2017/12/9
 */
public class SSLRequest extends Request {
    /**
     * HostnameVerifier
     * @see top.jfunc.common.http.base.Config#hostnameVerifier
     */
    private HostnameVerifier hostnameVerifier = new TrustAnyHostnameVerifier();
    /**
     * SSLContext
     * @see top.jfunc.common.http.base.Config#sslContext
     */
    private SSLContext sslContext = SSLSocketFactoryBuilder.create().getSSLContext();
    /**
     * SSLSocketFactory
     * @see top.jfunc.common.http.base.Config#sslSocketFactory
     */
    private SSLSocketFactory sslSocketFactory = null;
    /**
     * X509TrustManager
     * @see top.jfunc.common.http.base.Config#x509TrustManager
     */
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

    /**
     * 因为一般地 SslSocketFactory 都是从sslContext产生出来的 ， 所以如果没显式设置就从sslContext产生
     */
    public SSLSocketFactory getSslSocketFactory() {
        if(null == sslSocketFactory && null != sslContext){
            return sslContext.getSocketFactory();
        }
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
