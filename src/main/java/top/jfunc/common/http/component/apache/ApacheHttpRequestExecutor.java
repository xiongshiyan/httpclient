package top.jfunc.common.http.component.apache;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.response.ClientHttpResponse;

import java.io.IOException;

/**
 * Apache实现的处理
 * @author xiongshiyan
 * @since 2020.12.01
 * @since 1.2.12
 */
public class ApacheHttpRequestExecutor extends BaseHttpRequestExecutor<org.apache.http.HttpRequest, HttpResponse> implements HttpRequestExecutor<org.apache.http.HttpRequest> {
    private RequesterFactory<org.apache.http.HttpRequest> httpRequestRequesterFactory;
    private RequesterFactory<HttpClient> httpClientRequesterFactory;

    public ApacheHttpRequestExecutor() {
        super(new DefaultApacheHeaderHandler(), new DefaultApacheResponseStreamExtractor(), new DefaultApacheHeaderExtractor());
        this.httpRequestRequesterFactory = new DefaultApacheRequestFactory();
        this.httpClientRequesterFactory = new DefaultApacheClientFactory();
    }

    public ApacheHttpRequestExecutor(HeaderHandler<org.apache.http.HttpRequest> httpUriRequestHeaderHandler,
                                     StreamExtractor<HttpResponse> responseStreamExtractor,
                                     HeaderExtractor<HttpResponse> responseHeaderExtractor,
                                     RequesterFactory<org.apache.http.HttpRequest> httpRequestRequesterFactory,
                                     RequesterFactory<HttpClient> httpClientRequesterFactory) {
        super(httpUriRequestHeaderHandler, responseStreamExtractor, responseHeaderExtractor);
        this.httpRequestRequesterFactory = httpRequestRequesterFactory;
        this.httpClientRequesterFactory = httpClientRequesterFactory;
    }

    @Override
    public ClientHttpResponse execute(HttpRequest httpRequest, ContentCallback<org.apache.http.HttpRequest> contentCallback) throws IOException{
        //1.创建并配置
        org.apache.http.HttpRequest request = getHttpRequestRequesterFactory().create(httpRequest);

        //2.创建请求内容，如果有的话
        handleBody(request , contentCallback , httpRequest);

        //3.设置header
        handleHeaders(request, httpRequest);

        //4.创建HttpClient
        HttpClient httpClient = getHttpClientRequesterFactory().create(httpRequest);

        //5.发送请求
        HttpResponse response = getResponse(httpClient, request , httpRequest);

        return new ApacheClientHttpResponse(httpClient, response, httpRequest, getResponseStreamExtractor(), getResponseHeaderExtractor());
    }

    protected HttpResponse getResponse(HttpClient httpClient, org.apache.http.HttpRequest request , HttpRequest httpRequest) throws IOException {
        return httpClient.execute((HttpUriRequest) request  , HttpClientContext.create());
    }

    public RequesterFactory<org.apache.http.HttpRequest> getHttpRequestRequesterFactory() {
        return httpRequestRequesterFactory;
    }

    public RequesterFactory<HttpClient> getHttpClientRequesterFactory() {
        return httpClientRequesterFactory;
    }
}
