package top.jfunc.common.http.exe.okhttp3;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.okhttp3.*;
import top.jfunc.common.http.exe.BaseHttpRequestExecutor;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.exe.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

public class OkHttp3HttpRequestExecutor extends BaseHttpRequestExecutor<Request.Builder, Response> implements HttpRequestExecutor<Request.Builder> {
    private RequesterFactory<OkHttpClient> okHttpClientFactory;
    private RequesterFactory<Request.Builder> requestBuilderFactory;
    private HeaderHandler<Request.Builder> requestBuilderHeaderHandler;
    private RequestExecutor<OkHttpClient , Request , Response> requestExecutor;

    public OkHttp3HttpRequestExecutor() {
        super(new DefaultOkHttp3StreamExtractor(), new DefaultOkHttp3HeaderExtractor());
        setOkHttpClientFactory(new SingleOkHttp3ClientFactory());
        setRequestBuilderFactory(new DefaultOkHttp3RequestBuilderFactory());
        setRequestBuilderHeaderHandler(new DefaultOkHttp3HeaderHandler());
        setRequestExecutor(new DefaultOkHttp3RequestExecutor());
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
    protected void handleHeaders(Request.Builder builder , HttpRequest httpRequest) throws IOException {
        getRequestBuilderHeaderHandler().configHeaders(builder , httpRequest);
    }
    public RequesterFactory<OkHttpClient> getOkHttpClientFactory() {
        return okHttpClientFactory;
    }

    public void setOkHttpClientFactory(RequesterFactory<OkHttpClient> okHttpClientFactory) {
        this.okHttpClientFactory = okHttpClientFactory;
    }

    public RequesterFactory<Request.Builder> getRequestBuilderFactory() {
        return requestBuilderFactory;
    }

    public void setRequestBuilderFactory(RequesterFactory<Request.Builder> requestBuilderFactory) {
        this.requestBuilderFactory = requestBuilderFactory;
    }

    public HeaderHandler<Request.Builder> getRequestBuilderHeaderHandler() {
        return requestBuilderHeaderHandler;
    }

    public void setRequestBuilderHeaderHandler(HeaderHandler<Request.Builder> requestBuilderHeaderHandler) {
        this.requestBuilderHeaderHandler = requestBuilderHeaderHandler;
    }

    public RequestExecutor<OkHttpClient, Request, Response> getRequestExecutor() {
        return requestExecutor;
    }

    public void setRequestExecutor(RequestExecutor<OkHttpClient, Request, Response> requestExecutor) {
        this.requestExecutor = requestExecutor;
    }
}
