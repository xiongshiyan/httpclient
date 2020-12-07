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
    private HeaderHandler<CC> requestHeaderHandler;
    private StreamExtractor<RR> responseStreamExtractor;
    private HeaderExtractor<RR> responseHeaderExtractor;


    protected BaseHttpRequestExecutor(HeaderHandler<CC> requestHeaderHandler,
                                      StreamExtractor<RR> responseStreamExtractor,
                                      HeaderExtractor<RR> responseHeaderExtractor) {
        this.responseStreamExtractor = responseStreamExtractor;
        this.responseHeaderExtractor = responseHeaderExtractor;
        this.requestHeaderHandler = requestHeaderHandler;
    }

    protected void handleHeaders(CC requester , HttpRequest httpRequest) throws IOException {
        getRequestHeaderHandler().configHeaders(requester , httpRequest);
    }
    protected void handleBody(CC requester , ContentCallback<CC> contentCallback, HttpRequest httpRequest) throws IOException {
        if(null != contentCallback){
            contentCallback.doWriteWith(requester);
        }
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
