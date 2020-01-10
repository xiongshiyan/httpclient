package top.jfunc.common.http.smart;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.apache.*;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.InputStream;
import java.util.Objects;

/**
 * 使用Apache HttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheSmartHttpClient extends AbstractImplementSmartHttpClient<HttpEntityEnclosingRequest> {

    private RequesterFactory<HttpUriRequest> httpUriRequestRequesterFactory;
    private HeaderHandler<HttpUriRequest> httpUriRequestHeaderHandler;
    private RequesterFactory<CloseableHttpClient> closeableHttpClientRequesterFactory;
    private RequestExecutor<CloseableHttpClient , HttpUriRequest , CloseableHttpResponse> requestExecutor;
    ///private StreamExtractor<HttpEntity> httpEntityStreamExtractor;

    private StreamExtractor<CloseableHttpResponse> responseStreamExtractor;
    private HeaderExtractor<HttpResponse> httpResponseHeaderExtractor;

    public ApacheSmartHttpClient(){
        setBodyContentCallbackCreator(new DefaultApacheBodyContentCallbackCreator());
        setUploadContentCallbackCreator(new DefaultApacheUploadContentCallbackCreator());

        setHttpUriRequestRequesterFactory(new DefaultApacheRequestFactory());
        setHttpUriRequestHeaderHandler(new DefaultApacheHeaderHandler());
        setCloseableHttpClientRequesterFactory(new DefaultApacheClientFactory());
        setRequestExecutor(new DefaultApacheRequestExecutor());
        ///setHttpEntityStreamExtractor(new DefaultApacheEntityStreamExtractor());
        setResponseStreamExtractor(new DefaultApacheResponseStreamExtractor());
        setHttpResponseHeaderExtractor(new DefaultApacheHeaderExtractor());
    }

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, Method method , ContentCallback<HttpEntityEnclosingRequest> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        //2.创建并配置
        HttpUriRequest httpUriRequest = getHttpUriRequestRequesterFactory().create(httpRequest, method);

        //3.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            getContentCallbackHandler().handle((HttpEntityEnclosingRequest)httpUriRequest , contentCallback , httpRequest , method);
        }

        getHttpUriRequestHeaderHandler().configHeaders(httpUriRequest, httpRequest);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        ///HttpEntity entity = null;
        InputStream inputStream = null;
        try {
            httpClient = getCloseableHttpClientRequesterFactory().create(httpRequest, method);

            //4.发送请求
            response = getRequestExecutor().execute(httpClient , httpUriRequest);

            //5.处理返回值
            ///HttpEntity entity = response.getEntity();
            inputStream = getResponseStreamExtractor().extract(response , httpRequest);

            //6.处理headers
            MultiValueMap<String, String> responseHeaders = getHttpResponseHeaderExtractor().extract(response, httpRequest);

            return resultCallback.convert(response.getStatusLine().getStatusCode() , inputStream,
                    getConfig().getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    responseHeaders);
        }finally {
            IoUtil.close(inputStream);
            ///EntityUtils.consumeQuietly(entity);
            IoUtil.close(response);
            IoUtil.close(httpClient);
        }
    }


    public RequesterFactory<HttpUriRequest> getHttpUriRequestRequesterFactory() {
        return httpUriRequestRequesterFactory;
    }

    public void setHttpUriRequestRequesterFactory(RequesterFactory<HttpUriRequest> httpUriRequestRequesterFactory) {
        this.httpUriRequestRequesterFactory = Objects.requireNonNull(httpUriRequestRequesterFactory);
    }

    public HeaderHandler<HttpUriRequest> getHttpUriRequestHeaderHandler() {
        return httpUriRequestHeaderHandler;
    }

    public void setHttpUriRequestHeaderHandler(HeaderHandler<HttpUriRequest> httpUriRequestHeaderHandler) {
        this.httpUriRequestHeaderHandler = Objects.requireNonNull(httpUriRequestHeaderHandler);
    }

    public RequesterFactory<CloseableHttpClient> getCloseableHttpClientRequesterFactory() {
        return closeableHttpClientRequesterFactory;
    }

    public void setCloseableHttpClientRequesterFactory(RequesterFactory<CloseableHttpClient> closeableHttpClientRequesterFactory) {
        this.closeableHttpClientRequesterFactory = closeableHttpClientRequesterFactory;
    }

    public RequestExecutor<CloseableHttpClient, HttpUriRequest, CloseableHttpResponse> getRequestExecutor() {
        return requestExecutor;
    }

    public void setRequestExecutor(RequestExecutor<CloseableHttpClient, HttpUriRequest, CloseableHttpResponse> requestExecutor) {
        this.requestExecutor = Objects.requireNonNull(requestExecutor);
    }

    public StreamExtractor<CloseableHttpResponse> getResponseStreamExtractor() {
        return responseStreamExtractor;
    }

    public void setResponseStreamExtractor(StreamExtractor<CloseableHttpResponse> responseStreamExtractor) {
        this.responseStreamExtractor = responseStreamExtractor;
    }

    public HeaderExtractor<HttpResponse> getHttpResponseHeaderExtractor() {
        return httpResponseHeaderExtractor;
    }

    public void setHttpResponseHeaderExtractor(HeaderExtractor<HttpResponse> httpResponseHeaderExtractor) {
        this.httpResponseHeaderExtractor = Objects.requireNonNull(httpResponseHeaderExtractor);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by Apache's httpcomponents";
    }
}
