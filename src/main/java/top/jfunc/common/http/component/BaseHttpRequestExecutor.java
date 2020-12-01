package top.jfunc.common.http.component;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * 保存一些必须的共用的属性
 * @param <CC> 处理body的
 * @param <RR> 获取响应的header和stream的
 * @author xiongshiyan
 */
public abstract class BaseHttpRequestExecutor<CC,RR> {
    private ContentCallbackHandler<CC> contentCallbackHandler;
    private StreamExtractor<RR> responseStreamExtractor;
    private HeaderExtractor<RR> responseHeaderExtractor;
    private HeaderHandler<CC> requestHeaderHandler;


    protected BaseHttpRequestExecutor(StreamExtractor<RR> responseStreamExtractor,
                                      HeaderExtractor<RR> responseHeaderExtractor,
                                      HeaderHandler<CC> requestHeaderHandler) {
        this.contentCallbackHandler = new DefaultContentCallbackHandler<>();
        this.responseStreamExtractor = responseStreamExtractor;
        this.responseHeaderExtractor = responseHeaderExtractor;
        this.requestHeaderHandler = requestHeaderHandler;
    }

    public BaseHttpRequestExecutor(ContentCallbackHandler<CC> contentCallbackHandler,
                                   StreamExtractor<RR> responseStreamExtractor,
                                   HeaderExtractor<RR> responseHeaderExtractor,
                                   HeaderHandler<CC> requestHeaderHandler) {
        this.contentCallbackHandler = contentCallbackHandler;
        this.responseStreamExtractor = responseStreamExtractor;
        this.responseHeaderExtractor = responseHeaderExtractor;
        this.requestHeaderHandler = requestHeaderHandler;
    }

    protected void handleHeaders(CC requester , HttpRequest httpRequest) throws IOException {
        getRequestHeaderHandler().configHeaders(requester , httpRequest);
    }
    protected void handleBody(CC requester , ContentCallback<CC> contentCallback, HttpRequest httpRequest) throws IOException {
        getContentCallbackHandler().handle(requester , contentCallback , httpRequest);
    }

    public ContentCallbackHandler<CC> getContentCallbackHandler() {
        return contentCallbackHandler;
    }

    public StreamExtractor<RR> getResponseStreamExtractor() {
        return responseStreamExtractor;
    }

    public HeaderExtractor<RR> getResponseHeaderExtractor() {
        return responseHeaderExtractor;
    }

    public HeaderHandler<CC> getRequestHeaderHandler() {
        return requestHeaderHandler;
    }
}
