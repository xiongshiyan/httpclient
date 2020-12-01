package top.jfunc.common.http.component.okhttp3;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.BaseHttpRequestExecutor;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.component.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

public class OkHttp3HttpRequestExecutor extends BaseHttpRequestExecutor<Request.Builder, Response> implements HttpRequestExecutor<Request.Builder> {
    private RequesterFactory<OkHttpClient> okHttpClientFactory;
    private RequesterFactory<Request.Builder> requestBuilderFactory;
    private RequestExecutor<OkHttpClient , Request , Response> requestExecutor;

    public OkHttp3HttpRequestExecutor() {
        super(new DefaultOkHttp3StreamExtractor(), new DefaultOkHttp3HeaderExtractor(), new DefaultOkHttp3HeaderHandler());
        this.okHttpClientFactory = new SingleOkHttp3ClientFactory();
        this.requestBuilderFactory = new DefaultOkHttp3RequestBuilderFactory();
        this.requestExecutor = new DefaultOkHttp3RequestExecutor();
    }

    public OkHttp3HttpRequestExecutor(StreamExtractor<Response> responseStreamExtractor,
                                      HeaderExtractor<Response> responseHeaderExtractor,
                                      RequesterFactory<OkHttpClient> okHttpClientFactory,
                                      RequesterFactory<Request.Builder> requestBuilderFactory,
                                      HeaderHandler<Request.Builder> requestBuilderHeaderHandler,
                                      RequestExecutor<OkHttpClient, Request, Response> requestExecutor) {
        super(responseStreamExtractor, responseHeaderExtractor, requestBuilderHeaderHandler);
        this.okHttpClientFactory = okHttpClientFactory;
        this.requestBuilderFactory = requestBuilderFactory;
        this.requestExecutor = requestExecutor;
    }

    public OkHttp3HttpRequestExecutor(ContentCallbackHandler<Request.Builder> contentCallbackHandler,
                                      StreamExtractor<Response> responseStreamExtractor,
                                      HeaderExtractor<Response> responseHeaderExtractor,
                                      HeaderHandler<Request.Builder> requestHeaderHandler,
                                      RequesterFactory<OkHttpClient> okHttpClientFactory,
                                      RequesterFactory<Request.Builder> requestBuilderFactory,
                                      RequestExecutor<OkHttpClient, Request, Response> requestExecutor) {
        super(contentCallbackHandler,
                responseStreamExtractor,
                responseHeaderExtractor,
                requestHeaderHandler);
        this.okHttpClientFactory = okHttpClientFactory;
        this.requestBuilderFactory = requestBuilderFactory;
        this.requestExecutor = requestExecutor;
    }

    @Override
    public ClientHttpResponse execute(HttpRequest httpRequest, ContentCallback<Request.Builder> contentCallback) throws IOException{
        //1.创建并配置OkHttpClient
        OkHttpClient client = getOkHttpClientFactory().create(httpRequest);

        //2.1设置URL
        Request.Builder builder = getRequestBuilderFactory().create(httpRequest);

        //2.2处理请求体
        getContentCallbackHandler().handle(builder , contentCallback , httpRequest);

        //2.3设置headers
        handleHeaders(builder , httpRequest);

        //3.执行请求
        Response response = execute(client, httpRequest, builder);
        return new OkHttp3ClientHttpResponse(response, httpRequest, getResponseStreamExtractor(), getResponseHeaderExtractor());
    }
    protected Response execute(OkHttpClient client, HttpRequest httpRequest, Request.Builder builder) throws IOException {
        return getRequestExecutor().execute(client , builder.build() , httpRequest);
    }

    public RequesterFactory<OkHttpClient> getOkHttpClientFactory() {
        return okHttpClientFactory;
    }

    public RequesterFactory<Request.Builder> getRequestBuilderFactory() {
        return requestBuilderFactory;
    }

    public RequestExecutor<OkHttpClient, Request, Response> getRequestExecutor() {
        return requestExecutor;
    }
}
