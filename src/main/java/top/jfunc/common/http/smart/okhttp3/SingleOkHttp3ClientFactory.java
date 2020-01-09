package top.jfunc.common.http.smart.okhttp3;

import okhttp3.OkHttpClient;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.RequesterFactory;
import top.jfunc.common.http.util.OkHttp3Util;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 保持全局只有一个OkHttpClient，而不是每个请求都创建一个，提高性能
 * @see DefaultOkHttp3ClientFactory
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SingleOkHttp3ClientFactory implements RequesterFactory<OkHttpClient> {
    private OkHttpClient client;

    public SingleOkHttp3ClientFactory(){
        this.client = new OkHttpClient();
    }

    public SingleOkHttp3ClientFactory(OkHttpClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public OkHttpClient create(HttpRequest httpRequest, Method method, String completedUrl) throws IOException {

        Config config = httpRequest.getConfig();

        OkHttpClient.Builder clientBuilder = this.client.newBuilder();
        clientBuilder.connectTimeout(config.getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()) , TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeoutWithDefault(httpRequest.getReadTimeout()) , TimeUnit.MILLISECONDS);

        //1.1如果存在就设置代理
        ProxyInfo proxyInfo = config.getProxyInfoWithDefault(httpRequest.getProxyInfo());
        if(null != proxyInfo){
            clientBuilder.proxy(proxyInfo.getProxy());
        }
        //是否重定向
        clientBuilder.followRedirects(httpRequest.followRedirects());

        ////////////////////////////////////ssl处理///////////////////////////////////
        if(ParamUtil.isHttps(completedUrl)){
            initSSL(clientBuilder , httpRequest);
        }

        doWithBuilder(clientBuilder , httpRequest);


        this.client = clientBuilder.build();
        return this.client;
    }


    protected void initSSL(OkHttpClient.Builder clientBuilder , HttpRequest httpRequest){
        Config config = httpRequest.getConfig();
        OkHttp3Util.initSSL(clientBuilder , config.getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier()) ,
                config.getSSLSocketFactoryWithDefault(httpRequest.getSslSocketFactory()) ,
                config.getX509TrustManagerWithDefault(httpRequest.getX509TrustManager()));
    }


    protected void doWithBuilder(OkHttpClient.Builder clientBuilder , HttpRequest httpRequest){ }
}
