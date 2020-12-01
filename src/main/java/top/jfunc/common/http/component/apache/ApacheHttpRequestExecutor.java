package top.jfunc.common.http.component.apache;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.BaseHttpRequestExecutor;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.component.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * Apache实现的处理
 * @author xiongshiyan
 * @see 2020.12.01
 * @since 1.2.12
 */
public class ApacheHttpRequestExecutor extends BaseHttpRequestExecutor<org.apache.http.HttpRequest, org.apache.http.HttpResponse> implements HttpRequestExecutor<org.apache.http.HttpRequest> {
    private RequesterFactory<org.apache.http.HttpRequest> httpRequestRequesterFactory;
    private RequesterFactory<HttpClient> httpClientRequesterFactory;
    private RequestExecutor<HttpClient , org.apache.http.HttpRequest , org.apache.http.HttpResponse> requestExecutor;

    public ApacheHttpRequestExecutor() {
        super(new DefaultApacheResponseStreamExtractor(),
                new DefaultApacheHeaderExtractor(),
                new DefaultApacheHeaderHandler());
        this.httpRequestRequesterFactory = new DefaultApacheRequestFactory();
        this.httpClientRequesterFactory = new DefaultApacheClientFactory();
        this.requestExecutor = new DefaultApacheRequestExecutor();
    }

    public ApacheHttpRequestExecutor(StreamExtractor<HttpResponse> responseStreamExtractor,
                                     HeaderExtractor<HttpResponse> responseHeaderExtractor,
                                     RequesterFactory<org.apache.http.HttpRequest> httpRequestRequesterFactory,
                                     HeaderHandler<org.apache.http.HttpRequest> httpUriRequestHeaderHandler,
                                     RequesterFactory<HttpClient> httpClientRequesterFactory,
                                     RequestExecutor<HttpClient, org.apache.http.HttpRequest, HttpResponse> requestExecutor) {
        super(responseStreamExtractor,
                responseHeaderExtractor,
                httpUriRequestHeaderHandler);
        this.httpRequestRequesterFactory = httpRequestRequesterFactory;
        this.httpClientRequesterFactory = httpClientRequesterFactory;
        this.requestExecutor = requestExecutor;
    }

    public ApacheHttpRequestExecutor(ContentCallbackHandler<org.apache.http.HttpRequest> contentCallbackHandler,
                                     StreamExtractor<HttpResponse> responseStreamExtractor,
                                     HeaderExtractor<HttpResponse> responseHeaderExtractor,
                                     HeaderHandler<org.apache.http.HttpRequest> requestHeaderHandler,
                                     RequesterFactory<org.apache.http.HttpRequest> httpRequestRequesterFactory,
                                     RequesterFactory<HttpClient> httpClientRequesterFactory,
                                     RequestExecutor<HttpClient, org.apache.http.HttpRequest, HttpResponse> requestExecutor) {
        super(contentCallbackHandler,
                responseStreamExtractor,
                responseHeaderExtractor,
                requestHeaderHandler);
        this.httpRequestRequesterFactory = httpRequestRequesterFactory;
        this.httpClientRequesterFactory = httpClientRequesterFactory;
        this.requestExecutor = requestExecutor;
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
        HttpResponse response = execute(httpClient, request , httpRequest);

        return new ApacheClientHttpResponse(httpClient, response, httpRequest, getResponseStreamExtractor(), getResponseHeaderExtractor());
    }

    protected HttpResponse execute(HttpClient httpClient, org.apache.http.HttpRequest request , HttpRequest httpRequest) throws IOException {
        return getRequestExecutor().execute(httpClient , request , httpRequest);
    }

    public RequesterFactory<org.apache.http.HttpRequest> getHttpRequestRequesterFactory() {
        return httpRequestRequesterFactory;
    }

    public RequesterFactory<HttpClient> getHttpClientRequesterFactory() {
        return httpClientRequesterFactory;
    }

    public RequestExecutor<HttpClient, org.apache.http.HttpRequest, HttpResponse> getRequestExecutor() {
        return requestExecutor;
    }
}
