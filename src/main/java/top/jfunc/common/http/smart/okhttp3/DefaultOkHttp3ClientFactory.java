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
import java.util.concurrent.TimeUnit;

/**
 * @see SingleOkHttp3ClientFactory
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3ClientFactory implements RequesterFactory<OkHttpClient> {
    @Override
    public OkHttpClient create(HttpRequest httpRequest, Method method, String completedUrl) throws IOException {
        OkHttpClient.Builder clientBuilder = createAndConfigBuilder(httpRequest, completedUrl);
        return createOkHttpClient(clientBuilder , httpRequest);
    }

    protected OkHttpClient.Builder createAndConfigBuilder(HttpRequest httpRequest, String completedUrl) {
        Config config = httpRequest.getConfig();
        //1.构造OkHttpClient
        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(config.getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeoutWithDefault(httpRequest.getReadTimeout()), TimeUnit.MILLISECONDS);
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

        ////////////////////////////////////ssl处理///////////////////////////////////
        return clientBuilder;
    }

    protected void doWithBuilder(OkHttpClient.Builder clientBuilder , HttpRequest httpRequest){ }

    protected void initSSL(OkHttpClient.Builder clientBuilder , HttpRequest httpRequest){
        Config config = httpRequest.getConfig();
        OkHttp3Util.initSSL(clientBuilder , config.getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier()) ,
                config.getSSLSocketFactoryWithDefault(httpRequest.getSslSocketFactory()) ,
                config.getX509TrustManagerWithDefault(httpRequest.getX509TrustManager()));
    }

    /**
     * 子类复写，增添更多的功能，保证返回OkHttpClient
     */
    protected OkHttpClient createOkHttpClient(OkHttpClient.Builder builder , HttpRequest httpRequest) throws IOException{
        //默认就使用builder生成
        //可以进一步对builder进行处理
        OkHttpClient okHttpClient = builder.build();
        //对OkHttpClient单独处理
        doWithClient(okHttpClient , httpRequest);
        return okHttpClient;
    }

    /**
     * 子类对ObHttpClient复写
     */
    protected void doWithClient(OkHttpClient okHttpClient , HttpRequest httpRequest) throws IOException{
        //default do nothing, give children a chance to do more config
    }
}
