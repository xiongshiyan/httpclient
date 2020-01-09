package top.jfunc.common.http.smart;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.jodd.*;
import top.jfunc.common.utils.MultiValueMap;

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

    public JoddSmartHttpClient(){
        setBodyContentCallbackCreator(new DefaultJoddBodyContentCallbackCreator());
        setUploadContentCallbackCreator(new DefaultJoddUploadContentCallbackCreator());

        setHttpRequestRequesterFactory(new DefaultJoddHttpRequestFactory());
        setHttpRequestHeaderHandler(new DefaultJoddHeaderHandler());
        setRequestSender(new DefaultJoddSender());
        setHttpResponseStreamExtractor(new DefaultJoddStreamExtractor());
        setHttpResponseHeaderExtractor(new DefaultJoddHeaderExtractor());
    }

    @Override
    protected <R> R doInternalTemplate(top.jfunc.common.http.request.HttpRequest httpRequest, Method method , ContentCallback<HttpRequest> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        HttpResponse response = null;
        try {
            //1.获取完成的URL，创建请求
            String completedUrl = getCompletedUrlCreator().complete(httpRequest);

            HttpRequest request = getHttpRequestRequesterFactory().create(httpRequest, method, completedUrl);

            //4.处理body
            getContentCallbackHandler().handle(request , contentCallback , httpRequest , method);

            //5.设置header
            getHttpRequestHeaderHandler().configHeaders(request , httpRequest , completedUrl);

            //6.真正请求
            response = getRequestSender().send(request);

            //7.获取返回值
            InputStream inputStream = getHttpResponseStreamExtractor().extract(response, httpRequest , completedUrl);

            //8.返回header,包括Cookie处理
            MultiValueMap<String, String> responseHeaders = getHttpResponseHeaderExtractor().extract(response, httpRequest, completedUrl);

            return resultCallback.convert(response.statusCode(), inputStream,
                    getConfig().getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    responseHeaders);
        } finally {
            if(null != response){
                response.close();
            }
        }
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
}
