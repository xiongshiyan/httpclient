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
import java.net.CookieHandler;
import java.net.URI;

import static top.jfunc.common.http.util.ApacheUtil.*;
/**
 * 使用Apache HttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheSmartHttpClient extends AbstractSmartHttpClient<HttpEntityEnclosingRequest> {

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, Method method , ContentCallback<HttpEntityEnclosingRequest> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        //1.获取完整的URL
        /// ParamHolder queryParamHolder = httpRequest.queryParamHolder();
        /// RouteParamHolder routeParamHolder = httpRequest.routeParamHolder();
        /// String completedUrl = handleUrlIfNecessary(httpRequest.getUrl() , routeParamHolder.getMap() , queryParamHolder.getParams() , queryParamHolder.getParamCharset());
        String completedUrl = handleUrlIfNecessary(httpRequest.getUrl());

        //2.创建并配置
        HttpUriRequest httpUriRequest = createAndConfigHttpUriRequest(httpRequest, method, completedUrl);

        //3.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            if(contentCallback != null){
                contentCallback.doWriteWith((HttpEntityEnclosingRequest)httpUriRequest);
            }
        }

        configRequestHeaders(httpUriRequest, httpRequest, completedUrl);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        InputStream inputStream = null;
        try {
            HttpClientBuilder clientBuilder = createClientBuilder(httpRequest, completedUrl);

            //给子类复写的机会
            doWithClient(clientBuilder , httpRequest);

            httpClient = clientBuilder.build();
            //6.发送请求
            response = httpClient.execute(httpUriRequest  , HttpClientContext.create());
            int statusCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();

            //7.处理返回值
            inputStream = getStreamFrom(entity , httpRequest);

            //8.处理headers
            MultiValueMap<String, String> responseHeaders = parseResponseHeaders(response, httpRequest, completedUrl);

            return resultCallback.convert(statusCode , inputStream,
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

    protected MultiValueMap<String, String> parseResponseHeaders(CloseableHttpResponse response, HttpRequest httpRequest, String completedUrl) throws IOException {
        boolean includeHeaders = httpRequest.isIncludeHeaders();
        if(supportCookie()){
            includeHeaders = HttpRequest.INCLUDE_HEADERS;
        }
        MultiValueMap<String, String> parseHeaders = parseHeaders(response, includeHeaders);

        //存入Cookie
        if(supportCookie()){
            if(null != getCookieHandler() && null != parseHeaders){
                CookieHandler cookieHandler = getCookieHandler();
                cookieHandler.put(URI.create(completedUrl) , parseHeaders);
            }
        }
        return parseHeaders;
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

    protected void configRequestHeaders(HttpUriRequest httpUriRequest, HttpRequest httpRequest, String completedUrl) throws IOException {
        MultiValueMap<String, String> headers = mergeDefaultHeaders(httpRequest.getHeaders());

        //支持Cookie的话
        headers = handleCookieIfNecessary(completedUrl, headers);

        //4.设置请求头
        setRequestHeaders(httpUriRequest, httpRequest.getContentType(), headers);
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
