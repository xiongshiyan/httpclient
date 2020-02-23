package top.jfunc.common.http.component.okhttp3;

import okhttp3.OkHttpClient;
import top.jfunc.common.http.util.ParamUtil;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.component.AbstractRequesterFactory;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.OkHttp3Util;
import top.jfunc.common.utils.ObjectUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 保持全局只有一个OkHttpClient，而不是每个请求都创建一个，提高性能
 * @see DefaultOkHttp3ClientFactory
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SingleOkHttp3ClientFactory extends AbstractRequesterFactory<OkHttpClient> {
    private OkHttpClient client;

    public SingleOkHttp3ClientFactory(){
        this.client = new OkHttpClient();
    }

    public SingleOkHttp3ClientFactory(OkHttpClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public OkHttpClient doCreate(HttpRequest httpRequest) throws IOException {

        Config config = httpRequest.getConfig();

        OkHttpClient.Builder clientBuilder = this.client.newBuilder();
        clientBuilder.connectTimeout(config.getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()) , TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeoutWithDefault(httpRequest.getReadTimeout()) , TimeUnit.MILLISECONDS);

        //1.1如果存在就设置代理
        ProxyInfo proxyInfo = ObjectUtil.defaultIfNull(httpRequest.getProxyInfo(), config.getDefaultProxyInfo());
        if(null != proxyInfo){
            clientBuilder.proxy(proxyInfo.getProxy());
        }
        //是否重定向
        boolean followRedirects = ObjectUtil.defaultIfNull(httpRequest.followRedirects() , config.followRedirects());
        clientBuilder.followRedirects(followRedirects);

        ////////////////////////////////////ssl处理///////////////////////////////////
        if(ParamUtil.isHttps(httpRequest.getCompletedUrl())){
            initSSL(clientBuilder , httpRequest);
        }

        doWithBuilder(clientBuilder , httpRequest);


        this.client = clientBuilder.build();
        return this.client;
    }


    protected void initSSL(OkHttpClient.Builder clientBuilder , HttpRequest httpRequest){
        Config config = httpRequest.getConfig();
        HostnameVerifier hostnameVerifier = ObjectUtil.defaultIfNull(httpRequest.getHostnameVerifier(), config.sslHolder().getHostnameVerifier());
        SSLSocketFactory sslSocketFactory = ObjectUtil.defaultIfNull(httpRequest.getSslSocketFactory(), config.sslHolder().getSslSocketFactory());
        X509TrustManager x509TrustManager = ObjectUtil.defaultIfNull(httpRequest.getX509TrustManager(), config.sslHolder().getX509TrustManager());
        OkHttp3Util.initSSL(clientBuilder , hostnameVerifier , sslSocketFactory , x509TrustManager);
    }


    protected void doWithBuilder(OkHttpClient.Builder clientBuilder , HttpRequest httpRequest){ }
}
