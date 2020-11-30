package top.jfunc.common.http.exe.apache;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.apache.*;
import top.jfunc.common.http.exe.BaseHttpRequestExecutor;
import top.jfunc.common.http.exe.ClientHttpResponse;
import top.jfunc.common.http.exe.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

public class ApacheHttpRequestExecutor extends BaseHttpRequestExecutor<HttpEntityEnclosingRequest, HttpResponse> implements HttpRequestExecutor<HttpEntityEnclosingRequest> {
    private RequesterFactory<HttpUriRequest> httpUriRequestRequesterFactory;
    private HeaderHandler<HttpUriRequest> httpUriRequestHeaderHandler;
    private RequesterFactory<HttpClient> httpClientRequesterFactory;
    private RequestExecutor<HttpClient , HttpUriRequest , HttpResponse> requestExecutor;
    private Closer httpClientCloser;

    public ApacheHttpRequestExecutor() {
        super(new DefaultApacheResponseStreamExtractor(), new DefaultApacheHeaderExtractor());
        setHttpUriRequestRequesterFactory(new DefaultApacheRequestFactory());
        setHttpUriRequestHeaderHandler(new DefaultApacheHeaderHandler());
        setHttpClientRequesterFactory(new DefaultApacheClientFactory());
        setRequestExecutor(new DefaultApacheRequestExecutor());
        setHttpClientCloser(new DefaultCloser());
    }

    @Override
    public ClientHttpResponse execute(HttpRequest httpRequest, ContentCallback<HttpEntityEnclosingRequest> contentCallback) throws IOException{
        //1.创建并配置
        HttpUriRequest httpUriRequest = getHttpUriRequestRequesterFactory().create(httpRequest);

        //2.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            getContentCallbackHandler().handle((HttpEntityEnclosingRequest)httpUriRequest , contentCallback , httpRequest);
        }

        //3.设置header
        handleHeaders(httpUriRequest, httpRequest);

        HttpClient httpClient = getHttpClientRequesterFactory().create(httpRequest);

        //4.发送请求
        HttpResponse response = execute(httpClient, httpUriRequest , httpRequest);

        ApacheClientHttpResponse clientHttpResponse = new ApacheClientHttpResponse(response, httpRequest, getResponseStreamExtractor(), getResponseHeaderExtractor());

        closeHttpClient(httpClient);

        return clientHttpResponse;
    }
    protected void handleHeaders(HttpUriRequest httpUriRequest, HttpRequest httpRequest) throws IOException {
        getHttpUriRequestHeaderHandler().configHeaders(httpUriRequest, httpRequest);
    }

    protected HttpResponse execute(HttpClient httpClient, HttpUriRequest httpUriRequest , HttpRequest httpRequest) throws IOException {
        return getRequestExecutor().execute(httpClient , httpUriRequest , httpRequest);
    }

    protected void closeHttpClient(HttpClient httpClient) throws IOException {
        getHttpClientCloser().close(new HttpClientCloser(httpClient));
    }

    public RequesterFactory<HttpUriRequest> getHttpUriRequestRequesterFactory() {
        return httpUriRequestRequesterFactory;
    }

    public void setHttpUriRequestRequesterFactory(RequesterFactory<HttpUriRequest> httpUriRequestRequesterFactory) {
        this.httpUriRequestRequesterFactory = httpUriRequestRequesterFactory;
    }

    public HeaderHandler<HttpUriRequest> getHttpUriRequestHeaderHandler() {
        return httpUriRequestHeaderHandler;
    }

    public void setHttpUriRequestHeaderHandler(HeaderHandler<HttpUriRequest> httpUriRequestHeaderHandler) {
        this.httpUriRequestHeaderHandler = httpUriRequestHeaderHandler;
    }

    public RequesterFactory<HttpClient> getHttpClientRequesterFactory() {
        return httpClientRequesterFactory;
    }

    public void setHttpClientRequesterFactory(RequesterFactory<HttpClient> httpClientRequesterFactory) {
        this.httpClientRequesterFactory = httpClientRequesterFactory;
    }

    public RequestExecutor<HttpClient, HttpUriRequest, HttpResponse> getRequestExecutor() {
        return requestExecutor;
    }

    public void setRequestExecutor(RequestExecutor<HttpClient, HttpUriRequest, HttpResponse> requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    public Closer getHttpClientCloser() {
        return httpClientCloser;
    }

    public void setHttpClientCloser(Closer httpClientCloser) {
        this.httpClientCloser = httpClientCloser;
    }

    private static class HttpClientCloser extends AbstractCloseAdapter<HttpClient> {
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
}
