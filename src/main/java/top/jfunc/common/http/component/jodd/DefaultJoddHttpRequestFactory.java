package top.jfunc.common.http.component.jodd;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.component.AbstractRequesterFactory;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.JoddUtil;
import top.jfunc.common.utils.ObjectUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJoddHttpRequestFactory extends AbstractRequesterFactory<jodd.http.HttpRequest> {

    @Override
    public jodd.http.HttpRequest doCreate(HttpRequest httpRequest) throws IOException {
        Config config = httpRequest.getConfig();

        jodd.http.HttpRequest request = new jodd.http.HttpRequest();
        request.method(httpRequest.getMethod().name());
        request.set(httpRequest.getCompletedUrl());

        //2.超时设置
        request.connectionTimeout(config.getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()));
        request.timeout(config.getReadTimeoutWithDefault(httpRequest.getReadTimeout()));

        //是否重定向
        boolean followRedirects = ObjectUtil.defaultIfNull(httpRequest.followRedirects() , config.followRedirects());
        request.followRedirects(followRedirects);

        //3.SSL设置
        initSSL(request , httpRequest);

        return request;
    }

    protected void initSSL(jodd.http.HttpRequest request , HttpRequest httpRequest){
        Config config = httpRequest.getConfig();
        ProxyInfo proxyInfo = ObjectUtil.defaultIfNull(httpRequest.getProxyInfo(), config.getDefaultProxyInfo());
        HostnameVerifier hostnameVerifier = ObjectUtil.defaultIfNull(httpRequest.getHostnameVerifier(), config.sslHolder().getHostnameVerifier());
        SSLSocketFactory sslSocketFactory = ObjectUtil.defaultIfNull(httpRequest.getSslSocketFactory(), config.sslHolder().getSslSocketFactory());
        X509TrustManager x509TrustManager = ObjectUtil.defaultIfNull(httpRequest.getX509TrustManager(), config.sslHolder().getX509TrustManager());
        JoddUtil.initSSL(request , hostnameVerifier , sslSocketFactory , x509TrustManager, proxyInfo);
    }
}