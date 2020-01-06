package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.RequesterFactory;
import top.jfunc.common.http.util.JoddUtil;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJoddHttpRequestFactory implements RequesterFactory<jodd.http.HttpRequest> {

    @Override
    public jodd.http.HttpRequest create(HttpRequest httpRequest, Method method, String completedUrl) throws IOException {
        Config config = httpRequest.getConfig();

        jodd.http.HttpRequest request = new jodd.http.HttpRequest();
        request.method(method.name());
        request.set(completedUrl);

        //2.超时设置
        request.connectionTimeout(config.getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()));
        request.timeout(config.getReadTimeoutWithDefault(httpRequest.getReadTimeout()));

        //是否重定向
        request.followRedirects(httpRequest.followRedirects());

        //3.SSL设置
        initSSL(request , httpRequest);

        doWithHttpRequest(request , httpRequest);

        return request;
    }

    protected void initSSL(jodd.http.HttpRequest request , HttpRequest httpRequest){
        Config config = httpRequest.getConfig();
        JoddUtil.initSSL(request , config.getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier()) ,
                config.getSSLSocketFactoryWithDefault(httpRequest.getSslSocketFactory()) ,
                config.getX509TrustManagerWithDefault(httpRequest.getX509TrustManager()),
                config.getProxyInfoWithDefault(httpRequest.getProxyInfo()));
    }

    /**
     * 留给子类做更多的配置
     * @param joddHttpRequest jodd的请求
     * @param httpRequest 请求参数
     */
    protected void doWithHttpRequest(jodd.http.HttpRequest joddHttpRequest , top.jfunc.common.http.request.HttpRequest httpRequest){}
}