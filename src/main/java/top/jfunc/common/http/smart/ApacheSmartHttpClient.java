package top.jfunc.common.http.smart;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.URI;

import static top.jfunc.common.http.basic.ApacheUtil.*;
/**
 * 使用Apache HttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheSmartHttpClient extends AbstractSmartHttpClient<HttpEntityEnclosingRequest> implements SmartHttpClient, SmartInterceptorHttpTemplate<HttpEntityEnclosingRequest> {

    @Override
    public <R> R doTemplate(HttpRequest httpRequest, Method method , ContentCallback<HttpEntityEnclosingRequest> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        onBeforeIfNecessary(httpRequest, method);

        //1.获取完整的URL
        /// ParamHolder queryParamHolder = httpRequest.queryParamHolder();
        /// RouteParamHolder routeParamHolder = httpRequest.routeParamHolder();
        /// String completedUrl = handleUrlIfNecessary(httpRequest.getUrl() , routeParamHolder.getMap() , queryParamHolder.getParams() , queryParamHolder.getParamCharset());
        String completedUrl = handleUrlIfNecessary(httpRequest.getUrl());

        HttpUriRequest httpUriRequest = createHttpUriRequest(completedUrl, method);

        //2.设置请求参数
        setRequestProperty((HttpRequestBase) httpUriRequest,
                getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()),
                getReadTimeoutWithDefault(httpRequest.getReadTimeout()),
                getProxyInfoWithDefault(httpRequest.getProxyInfo()));

        //3.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            if(contentCallback != null){
                contentCallback.doWriteWith((HttpEntityEnclosingRequest)httpUriRequest);
            }
        }

        MultiValueMap<String, String> headers = mergeDefaultHeaders(httpRequest.getHeaders());

        //支持Cookie的话
        headers = handleCookieIfNecessary(completedUrl, headers);

        //4.设置请求头
        setRequestHeaders(httpUriRequest, httpRequest.getContentType(), headers ,
                httpRequest.getOverwriteHeaders());

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            ////////////////////////////////////ssl处理///////////////////////////////////
            HostnameVerifier hostnameVerifier = null;
            SSLContext sslContext = null;
            //https默认设置这些
            if(ParamUtil.isHttps(completedUrl)){
                hostnameVerifier = getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier());
                sslContext = getSSLContextWithDefault(httpRequest.getSslContext());
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            httpClient = getCloseableHttpClient(completedUrl , hostnameVerifier , sslContext);
            //6.发送请求
            response = httpClient.execute(httpUriRequest  , HttpClientContext.create());
            int statusCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();

            InputStream inputStream = getStreamFrom(entity , httpRequest.isIgnoreResponseBody());

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

            R convert = resultCallback.convert(statusCode , inputStream,
                    getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    parseHeaders);

            IoUtil.close(inputStream);

            onAfterReturnIfNecessary(httpRequest , convert);

            return convert;

        } catch (IOException e) {
            onErrorIfNecessary(httpRequest , e);
            throw e;
        } catch (Exception e){
            onErrorIfNecessary(httpRequest , e);
            throw new RuntimeException(e);
        }finally {
            onAfterIfNecessary(httpRequest);
            EntityUtils.consumeQuietly(entity);
            IoUtil.close(response);
            IoUtil.close(httpClient);
        }
    }
    @Override
    public  <R> R template(String url, Method method , String contentType, ContentCallback<HttpEntityEnclosingRequest> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , boolean includeHeader , ResultCallback<R> resultCallback) throws IOException {
        //1.获取完成的URL，创建请求
        String completedUrl = addBaseUrlIfNecessary(url);
        ///*URIBuilder builder = new URIBuilder(url);
        //URI uri = builder.build();*/
        HttpUriRequest httpUriRequest = createHttpUriRequest(completedUrl , method);


        //2.设置请求参数
        setRequestProperty((HttpRequestBase) httpUriRequest,
                getConnectionTimeoutWithDefault(connectTimeout),
                getReadTimeoutWithDefault(readTimeout));

        //3.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            if(contentCallback != null){
                contentCallback.doWriteWith((HttpEntityEnclosingRequest)httpUriRequest);
            }
        }

        //4.设置请求头
        setRequestHeaders(httpUriRequest, contentType, mergeDefaultHeaders(headers) , null);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {

            //5.创建http客户端
            ///CloseableHttpClient httpClient = HttpClients.createDefault();
            httpClient = getCloseableHttpClient(completedUrl ,getHostnameVerifier() , getSSLContext());

            //6.发送请求
            response = httpClient.execute(httpUriRequest  , HttpClientContext.create());
            int statusCode = response.getStatusLine().getStatusCode();
            /*String resultString = EntityUtils.toString(response.getEntity(), resultCharset);*/

            entity = response.getEntity();

            InputStream inputStream = getStreamFrom(entity, false);

            R convert = resultCallback.convert(statusCode , inputStream, getResultCharsetWithDefault(resultCharset),  parseHeaders(response , includeHeader));
            IoUtil.close(inputStream);

            return convert;
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            EntityUtils.consumeQuietly(entity);
            IoUtil.close(response);
            IoUtil.close(httpClient);
        }
    }

    @Override
    protected ContentCallback<HttpEntityEnclosingRequest> bodyContentCallback(String body, String bodyCharset, String contentType) throws IOException {
        return (request -> setRequestBody(request , body , bodyCharset));
    }

    @Override
    protected ContentCallback<HttpEntityEnclosingRequest> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, FormFile[] formFiles) throws IOException {
        return (request -> upload0(request, params , paramCharset , formFiles));
    }
    @Override
    public String toString() {
        return "SmartHttpClient implemented by Apache's httpcomponents";
    }
}
