package top.jfunc.common.http.smart;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.jodd.*;
import top.jfunc.common.http.util.JoddUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 使用Jodd-http 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class JoddSmartHttpClient extends AbstractImplementSmartHttpClient<HttpRequest> {

    private RequesterFactory<HttpRequest> httpRequestRequesterFactory;
    private HeaderHandler<HttpRequest> httpRequestHeaderHandler;
    private RequestSender<HttpRequest , HttpResponse> requestSender;
    private StreamExtractor<HttpResponse> httpResponseStreamExtractor;
    private HeaderExtractor<HttpResponse> httpResponseHeaderExtractor;

    @Override
    protected void init() {
        super.init();

        setBodyContentCallbackCreator(new DefaultJoddBodyContentCallbackCreator());
        setUploadContentCallbackCreator(new DefaultJoddUploadContentCallbackCreator());

        setHttpRequestRequesterFactory(new DefaultJoddHttpRequestFactory());
        setHttpRequestHeaderHandler(new DefaultJoddHeaderHandler());
        setRequestSender(new DefaultJoddSender());
        setHttpResponseStreamExtractor(new DefaultJoddStreamExtractor());
        setHttpResponseHeaderExtractor(new DefaultJoddHeaderExtractor());
    }

    @Override
    protected <R> R doInternalTemplate(top.jfunc.common.http.request.HttpRequest httpRequest , ContentCallback<HttpRequest> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        HttpResponse response = null;
        InputStream inputStream = null;
        try {
            //1.获取Request
            HttpRequest request = getHttpRequestRequesterFactory().create(httpRequest);

            //2.处理body
            getContentCallbackHandler().handle(request , contentCallback , httpRequest);

            //3.设置header
            handleHeaders(request , httpRequest);

            //4.真正请求
            response = send(request, httpRequest);

            //5.获取返回值
            inputStream = getHttpResponseStreamExtractor().extract(response, httpRequest);

            //6.返回header,包括Cookie处理
            MultiValueMap<String, String> responseHeaders = getHttpResponseHeaderExtractor().extract(response, httpRequest);

            //7.处理Cookie
            getCookieAccessor().saveCookieIfNecessary(httpRequest , responseHeaders);

            return resultCallback.convert(response.statusCode(), response.statusPhrase(),
                    inputStream, calculateResultCharset(httpRequest), responseHeaders);
        } finally {
            closeInputStream(inputStream);
            closeResponse(response);
        }
    }

    protected void handleHeaders(HttpRequest request , top.jfunc.common.http.request.HttpRequest httpRequest) throws IOException {
        getCookieAccessor().addCookieIfNecessary(httpRequest);
        getHttpRequestHeaderHandler().configHeaders(request , httpRequest);
    }

    protected HttpResponse send(HttpRequest request, top.jfunc.common.http.request.HttpRequest httpRequest) throws IOException {
        return getRequestSender().send(request , httpRequest);
    }

    protected void closeResponse(HttpResponse httpResponse) throws IOException {
        ///过度设计的嫌疑
        ///getResponseCloser().close(new HttpResponseCloser(httpResponse));
        JoddUtil.closeQuietly(httpResponse);
    }


    public RequesterFactory<HttpRequest> getHttpRequestRequesterFactory() {
        return httpRequestRequesterFactory;
    }

    public void setHttpRequestRequesterFactory(RequesterFactory<HttpRequest> httpRequestRequesterFactory) {
        this.httpRequestRequesterFactory = Objects.requireNonNull(httpRequestRequesterFactory);
    }

    public HeaderHandler<HttpRequest> getHttpRequestHeaderHandler() {
        return httpRequestHeaderHandler;
    }

    public void setHttpRequestHeaderHandler(HeaderHandler<HttpRequest> httpRequestHeaderHandler) {
        this.httpRequestHeaderHandler = Objects.requireNonNull(httpRequestHeaderHandler);
    }

    public RequestSender<HttpRequest, HttpResponse> getRequestSender() {
        return requestSender;
    }

    public void setRequestSender(RequestSender<HttpRequest, HttpResponse> requestSender) {
        this.requestSender = Objects.requireNonNull(requestSender);
    }

    public StreamExtractor<HttpResponse> getHttpResponseStreamExtractor() {
        return httpResponseStreamExtractor;
    }

    public void setHttpResponseStreamExtractor(StreamExtractor<HttpResponse> httpResponseStreamExtractor) {
        this.httpResponseStreamExtractor = Objects.requireNonNull(httpResponseStreamExtractor);
    }

    public HeaderExtractor<HttpResponse> getHttpResponseHeaderExtractor() {
        return httpResponseHeaderExtractor;
    }

    public void setHttpResponseHeaderExtractor(HeaderExtractor<HttpResponse> httpResponseHeaderExtractor) {
        this.httpResponseHeaderExtractor = Objects.requireNonNull(httpResponseHeaderExtractor);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by Jodd-Http";
    }



    /*protected static class HttpResponseCloser extends AbstractCloseAdapter<HttpResponse>{
        protected HttpResponseCloser(HttpResponse httpResponse){
            super(httpResponse);
        }
        @Override
        protected void doClose(HttpResponse httpResponse) throws IOException {
            httpResponse.close();
        }
    }*/
}
