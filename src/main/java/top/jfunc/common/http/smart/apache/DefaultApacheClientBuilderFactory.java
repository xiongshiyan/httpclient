package top.jfunc.common.http.smart.apache;

import org.apache.http.impl.client.HttpClientBuilder;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.AbstractRequesterFactory;
import top.jfunc.common.http.util.ApacheUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheClientBuilderFactory extends AbstractRequesterFactory<HttpClientBuilder> {
    @Override
    public HttpClientBuilder doCreate(HttpRequest httpRequest, Method method, String completedUrl) throws IOException {
        Config config = httpRequest.getConfig();
        ////////////////////////////////////ssl处理///////////////////////////////////
        HostnameVerifier hostnameVerifier = null;
        SSLContext sslContext = null;
        //https默认设置这些
        if(ParamUtil.isHttps(completedUrl)){
            hostnameVerifier = config.getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier());
            sslContext = config.getSSLContextWithDefault(httpRequest.getSslContext());
        }
        ////////////////////////////////////ssl处理///////////////////////////////////

        return ApacheUtil.getCloseableHttpClientBuilder(completedUrl, hostnameVerifier, sslContext, httpRequest.followRedirects());
    }
}
