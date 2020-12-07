package top.jfunc.common.http.component.jodd;

import jodd.http.HttpResponse;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.response.ClientHttpResponse;

import java.io.IOException;

/**
 * Jodd实现的处理
 * @author xiongshiyan
 * @since 2020.12.01
 * @since 1.2.12
 */
public class JoddHttpRequestExecutor extends BaseHttpRequestExecutor<jodd.http.HttpRequest, HttpResponse> implements HttpRequestExecutor<jodd.http.HttpRequest> {
    private RequesterFactory<jodd.http.HttpRequest> httpRequestRequesterFactory;

    public JoddHttpRequestExecutor() {
        super(new DefaultJoddHeaderHandler(), new DefaultJoddStreamExtractor(), new DefaultJoddHeaderExtractor());
        this.httpRequestRequesterFactory = new DefaultJoddHttpRequestFactory();
    }

    public JoddHttpRequestExecutor(HeaderHandler<jodd.http.HttpRequest> httpRequestHeaderHandler, StreamExtractor<HttpResponse> responseStreamExtractor,
                                   HeaderExtractor<HttpResponse> responseHeaderExtractor,
                                   RequesterFactory<jodd.http.HttpRequest> httpRequestRequesterFactory) {
        super(httpRequestHeaderHandler, responseStreamExtractor, responseHeaderExtractor);
        this.httpRequestRequesterFactory = httpRequestRequesterFactory;
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
        HttpResponse response = getResponse(request, httpRequest);
        return new JoddClientHttpResponse(response, httpRequest, getResponseStreamExtractor(), getResponseHeaderExtractor());
    }

    protected HttpResponse getResponse(jodd.http.HttpRequest request, top.jfunc.common.http.request.HttpRequest httpRequest) throws IOException {
        return request.send();
    }

    public RequesterFactory<jodd.http.HttpRequest> getHttpRequestRequesterFactory() {
        return httpRequestRequesterFactory;
    }
}
