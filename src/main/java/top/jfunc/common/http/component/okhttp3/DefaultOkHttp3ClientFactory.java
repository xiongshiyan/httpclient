package top.jfunc.common.http.component.okhttp3;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import top.jfunc.common.http.util.ParamUtil;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.component.AbstractRequesterFactory;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.OkHttp3Util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 每次都创建一个，会有性能问题，高并发的话很快就会报内存溢出
 * @see SingleOkHttp3ClientFactory
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3ClientFactory extends AbstractRequesterFactory<OkHttpClient> {
    @Override
    public OkHttpClient doCreate(HttpRequest httpRequest) throws IOException {
        return createAndConfigBuilder(httpRequest).build();
    }

    protected OkHttpClient.Builder createAndConfigBuilder(HttpRequest httpRequest) {
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
        if(ParamUtil.isHttps(httpRequest.getCompletedUrl())){
            initSSL(clientBuilder , httpRequest);
        }

        //因为每次都创建一个OkHttpClient，也就会创建一个连接池，如果高并发的话就会内存溢出
        clientBuilder.connectionPool(new ConnectionPool(5 , 30, TimeUnit.SECONDS));

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
}
