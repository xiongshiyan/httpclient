package top.jfunc.common.http.basic;

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
public class ApacheHttpClient extends AbstractImplementHttpClient<HttpEntityEnclosingRequest> {
    @Override
    public  <R> R doInternalTemplate(String url, Method method , String contentType, ContentCallback<HttpEntityEnclosingRequest> contentCallback, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset , boolean includeHeaders , ResultCallback<R> resultCallback) throws Exception {
        //1.获取完成的URL，创建请求
        String completedUrl = handleUrlIfNecessary(url);

        //2.创建并设置请求参数
        HttpUriRequest httpUriRequest = createAndConfigHttpUriRequest(method, completedUrl, connectTimeout, readTimeout);

        //3.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            if(contentCallback != null){
                contentCallback.doWriteWith((HttpEntityEnclosingRequest)httpUriRequest);
            }
        }

        //4.设置请求头
        configHeaders(httpUriRequest, completedUrl , contentType , headers);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        InputStream inputStream = null;
        try {

            //5.创建http客户端
            ///CloseableHttpClient httpClient = HttpClients.createDefault();
            ///HttpClientBuilder clientBuilder = getCloseableHttpClientBuilder(completedUrl, getHostnameVerifier(), getSSLContext());
            HttpClientBuilder clientBuilder = createClientBuilder(completedUrl);

            //给子类复写的机会
            doWithClient(clientBuilder);


            //6.发送请求
            httpClient = clientBuilder.build();
            response = httpClient.execute(httpUriRequest  , HttpClientContext.create());
            /*String resultString = EntityUtils.toString(response.getEntity(), resultCharset);*/

            //7.处理返回值
            entity = response.getEntity();
            inputStream = getStreamFrom(entity, false);

            MultiValueMap<String, String> responseHeaders = determineHeaders(response, completedUrl, includeHeaders);


            return resultCallback.convert(response.getStatusLine().getStatusCode() , inputStream,
                    getResultCharsetWithDefault(resultCharset),
                    responseHeaders);
        }finally {
            IoUtil.close(inputStream);
            EntityUtils.consumeQuietly(entity);
            IoUtil.close(response);
            IoUtil.close(httpClient);
        }
    }

    protected InputStream getStreamFrom(HttpEntity entity, boolean ignoreResponseBody) throws IOException {
        return ApacheUtil.getStreamFrom(entity, ignoreResponseBody);
    }

    @Override
    protected MultiValueMap<String, String> parseResponseHeaders(Object source, boolean includeHeaders) {
        return ApacheUtil.parseHeaders((CloseableHttpResponse) source , includeHeaders);
    }

    protected HttpClientBuilder createClientBuilder(String completedUrl) throws Exception {
        ////////////////////////////////////ssl处理///////////////////////////////////
        HostnameVerifier hostnameVerifier = null;
        SSLContext sslContext = null;
        //https默认设置这些
        if(ParamUtil.isHttps(completedUrl)){
            hostnameVerifier = getHostnameVerifierWithDefault(getHostnameVerifier());
            sslContext = getSSLContextWithDefault(getSSLContext());
        }
        ////////////////////////////////////ssl处理///////////////////////////////////

        return getCloseableHttpClientBuilder(completedUrl, hostnameVerifier, sslContext);
    }

    @Override
    protected void setRequestHeaders(Object target, String contentType , MultiValueMap<String, String> handledHeaders) throws IOException {
        ApacheUtil.setRequestHeaders((HttpUriRequest)target , contentType , handledHeaders);
    }

    protected HttpUriRequest createAndConfigHttpUriRequest(Method method, String completedUrl , int connectionTimeout , int readTimeout) {
        HttpUriRequest httpUriRequest = createHttpUriRequest(completedUrl, method);

        setRequestProperty((HttpRequestBase) httpUriRequest,
                getConnectionTimeoutWithDefault(connectionTimeout),
                getReadTimeoutWithDefault(readTimeout),
                getProxyInfoWithDefault(null));
        return httpUriRequest;
    }

    protected void doWithClient(HttpClientBuilder httpClientBuilder) throws Exception{
        //default do nothing, give children a chance to do more config
    }

    @Override
    protected ContentCallback<HttpEntityEnclosingRequest> bodyContentCallback(Method method , String body, String bodyCharset, String contentType) throws IOException {
        return request -> setRequestBody(request , body , bodyCharset);
    }

    @Override
    protected ContentCallback<HttpEntityEnclosingRequest> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        return request -> upload0(request, params, paramCharset, formFiles);
    }

    @Override
    public String toString() {
        return "HttpClient implemented by Apache's httpcomponents";
    }
}
