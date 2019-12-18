package top.jfunc.common.http.smart;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.ApacheUtil;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;

import static top.jfunc.common.http.util.ApacheUtil.*;

/**
 * 使用Apache HttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheSmartHttpClient extends AbstractImplementSmartHttpClient<HttpEntityEnclosingRequest> {

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, Method method , ContentCallback<HttpEntityEnclosingRequest> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        //1.获取完整的URL
        String completedUrl = handleUrlIfNecessary(httpRequest);

        //2.创建并配置
        HttpUriRequest httpUriRequest = createAndConfigHttpUriRequest(httpRequest, method, completedUrl);

        //3.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            if(contentCallback != null){
                contentCallback.doWriteWith((HttpEntityEnclosingRequest)httpUriRequest);
            }
        }

        configHeaders(httpUriRequest, httpRequest, completedUrl);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        InputStream inputStream = null;
        try {
            HttpClientBuilder clientBuilder = createClientBuilder(httpRequest, completedUrl);

            //给子类复写的机会
            doWithClient(clientBuilder , httpRequest);

            //6.发送请求
            httpClient = clientBuilder.build();
            response = httpClient.execute(httpUriRequest  , HttpClientContext.create());

            //7.处理返回值
            entity = response.getEntity();
            inputStream = getStreamFrom(entity , httpRequest);

            //8.处理headers
            MultiValueMap<String, String> responseHeaders = determineHeaders(response, httpRequest, completedUrl);

            return resultCallback.convert(response.getStatusLine().getStatusCode() , inputStream,
                    getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    responseHeaders);
        }finally {
            IoUtil.close(inputStream);
            EntityUtils.consumeQuietly(entity);
            IoUtil.close(response);
            IoUtil.close(httpClient);
        }
    }

    protected InputStream getStreamFrom(HttpEntity entity , HttpRequest httpRequest) throws IOException {
        return ApacheUtil.getStreamFrom(entity, httpRequest.isIgnoreResponseBody());
    }

    @Override
    protected MultiValueMap<String, String> parseResponseHeaders(Object source, HttpRequest httpRequest) {
        return ApacheUtil.parseHeaders((CloseableHttpResponse) source , httpRequest.isIncludeHeaders());
    }

    protected HttpClientBuilder createClientBuilder(HttpRequest httpRequest, String completedUrl) throws Exception {
        ////////////////////////////////////ssl处理///////////////////////////////////
        HostnameVerifier hostnameVerifier = null;
        SSLContext sslContext = null;
        //https默认设置这些
        if(ParamUtil.isHttps(completedUrl)){
            hostnameVerifier = getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier());
            sslContext = getSSLContextWithDefault(httpRequest.getSslContext());
        }
        ////////////////////////////////////ssl处理///////////////////////////////////

        return getCloseableHttpClientBuilder(completedUrl, hostnameVerifier, sslContext);
    }

    @Override
    protected void setRequestHeaders(Object target, HttpRequest httpRequest, MultiValueMap<String, String> handledHeaders) throws IOException {
        ApacheUtil.setRequestHeaders((HttpUriRequest)target , httpRequest.getContentType() , handledHeaders);
    }

    protected HttpUriRequest createAndConfigHttpUriRequest(HttpRequest httpRequest, Method method, String completedUrl) {
        HttpUriRequest httpUriRequest = createHttpUriRequest(completedUrl, method);

        //2.设置请求参数
        setRequestProperty((HttpRequestBase) httpUriRequest,
                getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()),
                getReadTimeoutWithDefault(httpRequest.getReadTimeout()),
                getProxyInfoWithDefault(httpRequest.getProxyInfo()));
        return httpUriRequest;
    }

    protected void doWithClient(HttpClientBuilder httpClientBuilder , HttpRequest httpRequest) throws Exception{
        //default do nothing, give children a chance to do more config
    }

    @Override
    protected ContentCallback<HttpEntityEnclosingRequest> bodyContentCallback(Method method , String body, String bodyCharset, String contentType) throws IOException {
        return request -> setRequestBody(request , body , bodyCharset);
    }

    @Override
    protected ContentCallback<HttpEntityEnclosingRequest> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        return request -> upload0(request, params , paramCharset , formFiles);
    }
    @Override
    public String toString() {
        return "SmartHttpClient implemented by Apache's httpcomponents";
    }
}
