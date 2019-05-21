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
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.ApacheHttpClient;
import top.jfunc.common.http.request.DownLoadRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.impl.GetRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.URI;

/**
 * 使用Apache HttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheSmartHttpClient extends ApacheHttpClient implements SmartHttpClient, SmartHttpTemplate<HttpEntityEnclosingRequest> {

    @Override
    public ApacheSmartHttpClient setConfig(Config config) {
        super.setConfig(config);
        return this;
    }

    @Override
    public <R> R template(HttpRequest httpRequest, Method method , ContentCallback<HttpEntityEnclosingRequest> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        //1.获取完整的URL
        String completedUrl = handleUrlIfNecessary(httpRequest.getUrl() , httpRequest.getRouteParams() ,httpRequest.getQueryParams() , httpRequest.getBodyCharset());

        HttpUriRequest httpUriRequest = createHttpUriRequest(completedUrl, method);

        //2.设置请求参数
        setRequestProperty((HttpRequestBase) httpUriRequest,
                getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()),
                getReadTimeoutWithDefault(httpRequest.getReadTimeout()),
                httpRequest.getProxyInfo());

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
        setRequestHeaders(httpUriRequest, httpRequest.getContentType(), headers);

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
                includeHeaders = top.jfunc.common.http.request.HttpRequest.INCLUDE_HEADERS;
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
    public Response get(HttpRequest req) throws IOException {
        HttpRequest request = beforeTemplate(req);
        Response response = template(request , Method.GET , null , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response post(StringBodyRequest req) throws IOException {
        StringBodyRequest request = beforeTemplate(req);
        String body = request.getBody();
        Response response = template(request, Method.POST ,
                r -> setRequestBody(r, body, getBodyCharsetWithDefault(request.getBodyCharset())), Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response httpMethod(HttpRequest req, Method method) throws IOException {
        HttpRequest httpRequest = beforeTemplate(req);
        ContentCallback<HttpEntityEnclosingRequest> contentCallback = null;
        if(method.hasContent() && httpRequest instanceof StringBodyRequest){
            String body = ((StringBodyRequest)httpRequest).getBody();
            contentCallback = r -> setRequestBody(r, body, getBodyCharsetWithDefault(httpRequest.getBodyCharset()));
        }
        Response response = template(httpRequest, method , contentCallback, Response::with);
        return afterTemplate(httpRequest , response);
    }

    @Override
    public byte[] getAsBytes(HttpRequest req) throws IOException {
        HttpRequest request = beforeTemplate(req);
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File download(DownLoadRequest req) throws IOException {
        DownLoadRequest request = beforeTemplate(req);
        return template(request , Method.GET, null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(UploadRequest req) throws IOException {
        UploadRequest request = beforeTemplate(req);
        Response response = template(request , Method.POST ,
                r -> addFormFiles(r, request.getFormParams(), request.getFormFiles()) , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response afterTemplate(HttpRequest request, Response response) throws IOException{
        if(request.isRedirectable() && response.needRedirect()){
            return get(GetRequest.of(response.getRedirectUrl()));
        }
        return response;
    }
}
