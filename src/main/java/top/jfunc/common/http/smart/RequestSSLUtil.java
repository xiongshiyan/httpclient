package top.jfunc.common.http.smart;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 *  SSL相关的一些配置，如果request中有就用request的，否则用默认的
 * @author xiongshiyan at 2019/4/2 , contact me with email yanshixiong@126.com or phone 15208384257
 */
class RequestSSLUtil {
    static X509TrustManager getX509TrustManager(Request request , X509TrustManager defaultX509TrustManager){
        if(request instanceof SSLRequest){
            SSLRequest sslRequest = (SSLRequest)request;
            X509TrustManager manager = sslRequest.getX509TrustManager();
            return null == manager ? defaultX509TrustManager : manager;
        }
        return defaultX509TrustManager;
    }
    static HostnameVerifier getHostnameVerifier(Request request , HostnameVerifier defaultHostnameVerifier){
        if(request instanceof SSLRequest){
            SSLRequest sslRequest = (SSLRequest)request;
            HostnameVerifier verifier = sslRequest.getHostnameVerifier();
            return null == verifier ? defaultHostnameVerifier : verifier;
        }
        return defaultHostnameVerifier;
    }
    static SSLContext getSSLContext(Request request , SSLContext defaultSslContext){
        if(request instanceof SSLRequest){
            SSLRequest sslRequest = (SSLRequest)request;
            SSLContext context = sslRequest.getSslContext();
            return null == context ? defaultSslContext : context;
        }
        return defaultSslContext;
    }

    static SSLSocketFactory getSSLSocketFactory(Request request , SSLSocketFactory defaultSSLSocketFactory){
        if(request instanceof SSLRequest){
            SSLRequest sslRequest = (SSLRequest)request;
            SSLSocketFactory factory = sslRequest.getSslSocketFactory();
            return null == factory ? defaultSSLSocketFactory : factory;
        }
        return defaultSSLSocketFactory;
    }
}
