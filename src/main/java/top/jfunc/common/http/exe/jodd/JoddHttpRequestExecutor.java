package top.jfunc.common.http.exe.jodd;

import jodd.http.HttpResponse;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.jodd.*;
import top.jfunc.common.http.exe.ClientHttpResponse;
import top.jfunc.common.http.exe.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

public class JoddHttpRequestExecutor implements HttpRequestExecutor<jodd.http.HttpRequest> {

    private RequesterFactory<jodd.http.HttpRequest> httpRequestRequesterFactory;
    private HeaderHandler<jodd.http.HttpRequest> httpRequestHeaderHandler;
    private RequestSender<jodd.http.HttpRequest, HttpResponse> requestSender;
    private StreamExtractor<HttpResponse> httpResponseStreamExtractor;
    private HeaderExtractor<HttpResponse> httpResponseHeaderExtractor;
    private ContentCallbackHandler<jodd.http.HttpRequest> contentCallbackHandler;

    public JoddHttpRequestExecutor() {
        init();
    }

    protected void init(){
        setHttpRequestRequesterFactory(new DefaultJoddHttpRequestFactory());
        setHttpRequestHeaderHandler(new DefaultJoddHeaderHandler());
        setRequestSender(new DefaultJoddSender());
        setHttpResponseStreamExtractor(new DefaultJoddStreamExtractor());
        setHttpResponseHeaderExtractor(new DefaultJoddHeaderExtractor());

        setContentCallbackHandler(new DefaultContentCallbackHandler<>());
    }

    @Override
    public ClientHttpResponse execute(HttpRequest httpRequest, ContentCallback<jodd.http.HttpRequest> contentCallback) throws IOException{
        //1.获取Request
        jodd.http.HttpRequest request = getHttpRequestRequesterFactory().create(httpRequest);

        //2.处理body
        getContentCallbackHandler().handle(request , contentCallback , httpRequest);

        //3.设置header
        handleHeaders(request , httpRequest);

        //4.真正请求
        HttpResponse response = send(request, httpRequest);
        return new JoddClientHttpResponse(response, httpRequest, getHttpResponseStreamExtractor(), getHttpResponseHeaderExtractor());
    }
    protected void handleHeaders(jodd.http.HttpRequest request , top.jfunc.common.http.request.HttpRequest httpRequest) throws IOException {
        getHttpRequestHeaderHandler().configHeaders(request , httpRequest);
    }

    protected HttpResponse send(jodd.http.HttpRequest request, top.jfunc.common.http.request.HttpRequest httpRequest) throws IOException {
        return getRequestSender().send(request , httpRequest);
    }

    public RequesterFactory<jodd.http.HttpRequest> getHttpRequestRequesterFactory() {
        return httpRequestRequesterFactory;
    }

    public void setHttpRequestRequesterFactory(RequesterFactory<jodd.http.HttpRequest> httpRequestRequesterFactory) {
        this.httpRequestRequesterFactory = httpRequestRequesterFactory;
    }

    public HeaderHandler<jodd.http.HttpRequest> getHttpRequestHeaderHandler() {
        return httpRequestHeaderHandler;
    }

    public void setHttpRequestHeaderHandler(HeaderHandler<jodd.http.HttpRequest> httpRequestHeaderHandler) {
        this.httpRequestHeaderHandler = httpRequestHeaderHandler;
    }

    public RequestSender<jodd.http.HttpRequest, HttpResponse> getRequestSender() {
        return requestSender;
    }

    public void setRequestSender(RequestSender<jodd.http.HttpRequest, HttpResponse> requestSender) {
        this.requestSender = requestSender;
    }

    public StreamExtractor<HttpResponse> getHttpResponseStreamExtractor() {
        return httpResponseStreamExtractor;
    }

    public void setHttpResponseStreamExtractor(StreamExtractor<HttpResponse> httpResponseStreamExtractor) {
        this.httpResponseStreamExtractor = httpResponseStreamExtractor;
    }

    public HeaderExtractor<HttpResponse> getHttpResponseHeaderExtractor() {
        return httpResponseHeaderExtractor;
    }

    public void setHttpResponseHeaderExtractor(HeaderExtractor<HttpResponse> httpResponseHeaderExtractor) {
        this.httpResponseHeaderExtractor = httpResponseHeaderExtractor;
    }

    public ContentCallbackHandler<jodd.http.HttpRequest> getContentCallbackHandler() {
        return contentCallbackHandler;
    }

    public void setContentCallbackHandler(ContentCallbackHandler<jodd.http.HttpRequest> contentCallbackHandler) {
        this.contentCallbackHandler = contentCallbackHandler;
    }
}
