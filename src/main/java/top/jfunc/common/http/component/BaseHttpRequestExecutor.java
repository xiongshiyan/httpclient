package top.jfunc.common.http.component;

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


    protected BaseHttpRequestExecutor(StreamExtractor<RR> responseStreamExtractor, HeaderExtractor<RR> responseHeaderExtractor) {
        this.contentCallbackHandler = new DefaultContentCallbackHandler<>();
        this.responseStreamExtractor = responseStreamExtractor;
        this.responseHeaderExtractor = responseHeaderExtractor;
    }


    public ContentCallbackHandler<CC> getContentCallbackHandler() {
        return contentCallbackHandler;
    }

    public void setContentCallbackHandler(ContentCallbackHandler<CC> contentCallbackHandler) {
        this.contentCallbackHandler = contentCallbackHandler;
    }

    public StreamExtractor<RR> getResponseStreamExtractor() {
        return responseStreamExtractor;
    }

    public void setResponseStreamExtractor(StreamExtractor<RR> responseStreamExtractor) {
        this.responseStreamExtractor = responseStreamExtractor;
    }

    public HeaderExtractor<RR> getResponseHeaderExtractor() {
        return responseHeaderExtractor;
    }

    public void setResponseHeaderExtractor(HeaderExtractor<RR> responseHeaderExtractor) {
        this.responseHeaderExtractor = responseHeaderExtractor;
    }
}
