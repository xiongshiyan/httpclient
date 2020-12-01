package top.jfunc.common.http.component.jodd;

import jodd.http.HttpResponse;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.BaseHttpRequestExecutor;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.component.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * Jodd实现的处理
 * @author xiongshiyan
 * @see 2020.12.01
 * @since 1.2.12
 */
public class JoddHttpRequestExecutor extends BaseHttpRequestExecutor<jodd.http.HttpRequest, HttpResponse> implements HttpRequestExecutor<jodd.http.HttpRequest> {
    private RequesterFactory<jodd.http.HttpRequest> httpRequestRequesterFactory;
    private RequestSender<jodd.http.HttpRequest, HttpResponse> requestSender;

    public JoddHttpRequestExecutor() {
        super(new DefaultJoddStreamExtractor(), new DefaultJoddHeaderExtractor(), new DefaultJoddHeaderHandler());
        this.httpRequestRequesterFactory = new DefaultJoddHttpRequestFactory();
        this.requestSender = new DefaultJoddSender();
    }

    public JoddHttpRequestExecutor(StreamExtractor<HttpResponse> responseStreamExtractor,
                                   HeaderExtractor<HttpResponse> responseHeaderExtractor,
                                   RequesterFactory<jodd.http.HttpRequest> httpRequestRequesterFactory,
                                   HeaderHandler<jodd.http.HttpRequest> httpRequestHeaderHandler,
                                   RequestSender<jodd.http.HttpRequest, HttpResponse> requestSender) {
        super(responseStreamExtractor, responseHeaderExtractor, httpRequestHeaderHandler);
        this.httpRequestRequesterFactory = httpRequestRequesterFactory;
        this.requestSender = requestSender;
    }

    public JoddHttpRequestExecutor(ContentCallbackHandler<jodd.http.HttpRequest> contentCallbackHandler,
                                   StreamExtractor<HttpResponse> responseStreamExtractor,
                                   HeaderExtractor<HttpResponse> responseHeaderExtractor,
                                   HeaderHandler<jodd.http.HttpRequest> requestHeaderHandler,
                                   RequesterFactory<jodd.http.HttpRequest> httpRequestRequesterFactory,
                                   RequestSender<jodd.http.HttpRequest, HttpResponse> requestSender) {
        super(contentCallbackHandler,
                responseStreamExtractor,
                responseHeaderExtractor,
                requestHeaderHandler);
        this.httpRequestRequesterFactory = httpRequestRequesterFactory;
        this.requestSender = requestSender;
    }

    @Override
    public ClientHttpResponse execute(HttpRequest httpRequest, ContentCallback<jodd.http.HttpRequest> contentCallback) throws IOException{
        //1.获取Request
        jodd.http.HttpRequest request = getHttpRequestRequesterFactory().create(httpRequest);

        //2.处理body
        handleBody(request , contentCallback , httpRequest);

        //3.设置header
        handleHeaders(request , httpRequest);

        //4.真正请求
        HttpResponse response = send(request, httpRequest);
        return new JoddClientHttpResponse(response, httpRequest, getResponseStreamExtractor(), getResponseHeaderExtractor());
    }

    protected HttpResponse send(jodd.http.HttpRequest request, top.jfunc.common.http.request.HttpRequest httpRequest) throws IOException {
        return getRequestSender().send(request , httpRequest);
    }

    public RequesterFactory<jodd.http.HttpRequest> getHttpRequestRequesterFactory() {
        return httpRequestRequesterFactory;
    }

    public RequestSender<jodd.http.HttpRequest, HttpResponse> getRequestSender() {
        return requestSender;
    }
}
