package top.jfunc.common.http.smart;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.apache.*;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 使用Apache HttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheSmartHttpClient extends AbstractImplementSmartHttpClient<HttpEntityEnclosingRequest> {

    private RequesterFactory<HttpUriRequest> httpUriRequestRequesterFactory;
    private HeaderHandler<HttpUriRequest> httpUriRequestHeaderHandler;
    private RequesterFactory<HttpClient> httpClientRequesterFactory;
    private RequestExecutor<HttpClient , HttpUriRequest , HttpResponse> requestExecutor;
    ///private StreamExtractor<HttpEntity> httpEntityStreamExtractor;

    private StreamExtractor<HttpResponse> responseStreamExtractor;
    private HeaderExtractor<HttpResponse> httpResponseHeaderExtractor;

    private Closer httpResponseCloser;
    private Closer httpClientCloser;

    public ApacheSmartHttpClient(){
        setBodyContentCallbackCreator(new DefaultApacheBodyContentCallbackCreator());
        setUploadContentCallbackCreator(new DefaultApacheUploadContentCallbackCreator());

        setHttpUriRequestRequesterFactory(new DefaultApacheRequestFactory());
        setHttpUriRequestHeaderHandler(new DefaultApacheHeaderHandler());
        setHttpClientRequesterFactory(new DefaultApacheClientFactory());
        setRequestExecutor(new DefaultApacheRequestExecutor());
        ///setHttpEntityStreamExtractor(new DefaultApacheEntityStreamExtractor());
        setResponseStreamExtractor(new DefaultApacheResponseStreamExtractor());
        setHttpResponseHeaderExtractor(new DefaultApacheHeaderExtractor());


        setHttpResponseCloser(new DefaultCloser());
        setHttpClientCloser(new DefaultCloser());
    }

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest , ContentCallback<HttpEntityEnclosingRequest> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        //1.创建并配置
        HttpUriRequest httpUriRequest = getHttpUriRequestRequesterFactory().create(httpRequest);

        //2.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            getContentCallbackHandler().handle((HttpEntityEnclosingRequest)httpUriRequest , contentCallback , httpRequest);
        }

        //3.设置header
        handleHeaders(httpUriRequest, httpRequest);

        HttpClient httpClient = null;
        HttpResponse response = null;
        InputStream inputStream = null;
        try {
            httpClient = getHttpClientRequesterFactory().create(httpRequest);

            //4.发送请求
            response = execute(httpClient, httpUriRequest , httpRequest);

            //5.处理返回值
            inputStream = getResponseStreamExtractor().extract(response , httpRequest);

            //6.处理headers
            MultiValueMap<String, String> responseHeaders = getHttpResponseHeaderExtractor().extract(response, httpRequest);

            //7.处理Cookie
            getCookieAccessor().saveCookieIfNecessary(httpRequest, responseHeaders);

            return resultCallback.convert(response.getStatusLine().getStatusCode() , inputStream,
                    getConfig().getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    responseHeaders);
        }finally {
            closeInputStream(inputStream);
            closeResponse(response);
            closeHttpClient(httpClient);
        }
    }

    private void handleHeaders(HttpUriRequest httpUriRequest, HttpRequest httpRequest) throws IOException {
        getCookieAccessor().addCookieIfNecessary(httpRequest);
        getHttpUriRequestHeaderHandler().configHeaders(httpUriRequest, httpRequest);
    }

    protected HttpResponse execute(HttpClient httpClient, HttpUriRequest httpUriRequest , HttpRequest httpRequest) throws IOException {
        return getRequestExecutor().execute(httpClient , httpUriRequest , httpRequest);
    }

    protected void closeInputStream(InputStream inputStream) throws IOException {
        getInputStreamCloser().close(inputStream);
    }

    protected void closeResponse(HttpResponse response) throws IOException {
        getHttpResponseCloser().close(new HttpResponseCloser(response));
    }
    protected void closeHttpClient(HttpClient httpClient) throws IOException {
        getHttpClientCloser().close(new HttpClientCloser(httpClient));
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

    public RequesterFactory<HttpClient> getHttpClientRequesterFactory() {
        return httpClientRequesterFactory;
    }

    public void setHttpClientRequesterFactory(RequesterFactory<HttpClient> httpClientRequesterFactory) {
        this.httpClientRequesterFactory = Objects.requireNonNull(httpClientRequesterFactory);
    }

    public RequestExecutor<HttpClient, HttpUriRequest, HttpResponse> getRequestExecutor() {
        return requestExecutor;
    }

    public void setRequestExecutor(RequestExecutor<HttpClient, HttpUriRequest, HttpResponse> requestExecutor) {
        this.requestExecutor = Objects.requireNonNull(requestExecutor);
    }

    public StreamExtractor<HttpResponse> getResponseStreamExtractor() {
        return responseStreamExtractor;
    }

    public void setResponseStreamExtractor(StreamExtractor<HttpResponse> responseStreamExtractor) {
        this.responseStreamExtractor = Objects.requireNonNull(responseStreamExtractor);
    }

    public HeaderExtractor<HttpResponse> getHttpResponseHeaderExtractor() {
        return httpResponseHeaderExtractor;
    }

    public void setHttpResponseHeaderExtractor(HeaderExtractor<HttpResponse> httpResponseHeaderExtractor) {
        this.httpResponseHeaderExtractor = Objects.requireNonNull(httpResponseHeaderExtractor);
    }

    public Closer getHttpResponseCloser() {
        return httpResponseCloser;
    }

    public void setHttpResponseCloser(Closer httpResponseCloser) {
        this.httpResponseCloser = Objects.requireNonNull(httpResponseCloser);
    }

    public Closer getHttpClientCloser() {
        return httpClientCloser;
    }

    public void setHttpClientCloser(Closer httpClientCloser) {
        this.httpClientCloser = Objects.requireNonNull(httpClientCloser);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by Apache's httpcomponents";
    }



    private static class HttpClientCloser extends AbstractCloseAdapter<HttpClient>{
        private HttpClientCloser(HttpClient httpClient){
            super(httpClient);
        }
        @Override
        protected void doClose(HttpClient httpClient) throws IOException {
            if(httpClient instanceof CloseableHttpClient){
                ((CloseableHttpClient) httpClient).close();
            }
        }
    }
    protected static class HttpResponseCloser extends AbstractCloseAdapter<HttpResponse>{
        protected HttpResponseCloser(HttpResponse httpResponse){
            super(httpResponse);
        }
        @Override
        protected void doClose(HttpResponse httpResponse) throws IOException {
            if(httpResponse instanceof CloseableHttpResponse){
                ((CloseableHttpResponse) httpResponse).close();
            }
        }
    }
}
