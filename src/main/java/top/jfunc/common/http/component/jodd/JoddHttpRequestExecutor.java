package top.jfunc.common.http.component.jodd;

import jodd.http.HttpResponse;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.BaseHttpRequestExecutor;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.component.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

public class JoddHttpRequestExecutor extends BaseHttpRequestExecutor<jodd.http.HttpRequest, HttpResponse> implements HttpRequestExecutor<jodd.http.HttpRequest> {

    private RequesterFactory<jodd.http.HttpRequest> httpRequestRequesterFactory;
    private HeaderHandler<jodd.http.HttpRequest> httpRequestHeaderHandler;
    private RequestSender<jodd.http.HttpRequest, HttpResponse> requestSender;

    public JoddHttpRequestExecutor() {
        super(new DefaultJoddStreamExtractor(), new DefaultJoddHeaderExtractor());
        setHttpRequestRequesterFactory(new DefaultJoddHttpRequestFactory());
        setHttpRequestHeaderHandler(new DefaultJoddHeaderHandler());
        setRequestSender(new DefaultJoddSender());
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
        return new JoddClientHttpResponse(response, httpRequest, getResponseStreamExtractor(), getResponseHeaderExtractor());
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
}
