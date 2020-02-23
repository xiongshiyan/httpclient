package top.jfunc.common.http.component.apache;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import top.jfunc.common.http.util.ParamUtil;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.component.AbstractRequesterFactory;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.ApacheUtil;
import top.jfunc.common.utils.ObjectUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheClientFactory extends AbstractRequesterFactory<HttpClient> {
    @Override
    public HttpClient doCreate(HttpRequest httpRequest) throws IOException {
        Config config = httpRequest.getConfig();
        ////////////////////////////////////ssl处理///////////////////////////////////
        HostnameVerifier hostnameVerifier = null;
        SSLContext sslContext = null;
        String completedUrl = httpRequest.getCompletedUrl();
        //https默认设置这些
        if(ParamUtil.isHttps(completedUrl)){
            hostnameVerifier = ObjectUtil.defaultIfNull(httpRequest.getHostnameVerifier(), config.sslHolder().getHostnameVerifier());
            sslContext = ObjectUtil.defaultIfNull(httpRequest.getSslContext(), config.sslHolder().getSslContext());
        }
        ////////////////////////////////////ssl处理///////////////////////////////////

        boolean followRedirects = ObjectUtil.defaultIfNull(httpRequest.followRedirects() , config.followRedirects());
        HttpClientBuilder clientBuilder = ApacheUtil.getCloseableHttpClientBuilder(completedUrl, hostnameVerifier, sslContext, followRedirects);
        return clientBuilder.build();
    }
}
