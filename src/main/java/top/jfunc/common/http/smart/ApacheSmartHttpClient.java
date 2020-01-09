package top.jfunc.common.http.smart;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.apache.*;
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
    private RequesterFactory<HttpClientBuilder> clientBuilderRequesterFactory;
    private RequestExecutor<CloseableHttpClient , HttpUriRequest , CloseableHttpResponse> requestExecutor;
    private StreamExtractor<HttpEntity> httpEntityStreamExtractor;
    private HeaderExtractor<HttpResponse> httpResponseHeaderExtractor;

    public ApacheSmartHttpClient(){
        setBodyContentCallbackCreator(new DefaultApacheBodyContentCallbackCreator());
        setUploadContentCallbackCreator(new DefaultApacheUploadContentCallbackCreator());

        setHttpUriRequestRequesterFactory(new DefaultApacheRequestFactory());
        setHttpUriRequestHeaderHandler(new DefaultApacheHeaderHandler());
        setClientBuilderRequesterFactory(new DefaultApacheClientBuilderFactory());
        setRequestExecutor(new DefaultApacheRequestExecutor());
        setHttpEntityStreamExtractor(new DefaultApacheStreamExtractor());
        setHttpResponseHeaderExtractor(new DefaultApacheHeaderExtractor());
    }

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, Method method , ContentCallback<HttpEntityEnclosingRequest> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        //1.获取完整的URL
        String completedUrl = getCompletedUrlCreator().complete(httpRequest);

        //2.创建并配置
        HttpUriRequest httpUriRequest = getHttpUriRequestRequesterFactory().create(httpRequest, method, completedUrl);

        //3.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            getContentCallbackHandler().handle((HttpEntityEnclosingRequest)httpUriRequest , contentCallback , httpRequest , method);
        }

        getHttpUriRequestHeaderHandler().configHeaders(httpUriRequest, httpRequest, completedUrl);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        InputStream inputStream = null;
        try {
            HttpClientBuilder clientBuilder = getClientBuilderRequesterFactory().create(httpRequest, method ,completedUrl);

            //4.发送请求
            httpClient = clientBuilder.build();
            response = getRequestExecutor().execute(httpClient , httpUriRequest);

            //5.处理返回值
            entity = response.getEntity();
            inputStream = getHttpEntityStreamExtractor().extract(entity , httpRequest , completedUrl);

            //6.处理headers
            MultiValueMap<String, String> responseHeaders = getHttpResponseHeaderExtractor().extract(response, httpRequest, completedUrl);

            return resultCallback.convert(response.getStatusLine().getStatusCode() , inputStream,
                    getConfig().getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    responseHeaders);
        }finally {
            IoUtil.close(inputStream);
            EntityUtils.consumeQuietly(entity);
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

    public RequesterFactory<HttpClientBuilder> getClientBuilderRequesterFactory() {
        return clientBuilderRequesterFactory;
    }

    public void setClientBuilderRequesterFactory(RequesterFactory<HttpClientBuilder> clientBuilderRequesterFactory) {
        this.clientBuilderRequesterFactory = Objects.requireNonNull(clientBuilderRequesterFactory);
    }

    public RequestExecutor<CloseableHttpClient, HttpUriRequest, CloseableHttpResponse> getRequestExecutor() {
        return requestExecutor;
    }

    public void setRequestExecutor(RequestExecutor<CloseableHttpClient, HttpUriRequest, CloseableHttpResponse> requestExecutor) {
        this.requestExecutor = Objects.requireNonNull(requestExecutor);
    }

    public StreamExtractor<HttpEntity> getHttpEntityStreamExtractor() {
        return httpEntityStreamExtractor;
    }

    public void setHttpEntityStreamExtractor(StreamExtractor<HttpEntity> httpEntityStreamExtractor) {
        this.httpEntityStreamExtractor = Objects.requireNonNull(httpEntityStreamExtractor);
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
