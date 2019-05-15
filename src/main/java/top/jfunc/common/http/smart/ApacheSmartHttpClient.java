package top.jfunc.common.http.smart;

import org.apache.http.HttpEntity;
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
import top.jfunc.common.utils.IoUtil;
import org.apache.http.HttpEntityEnclosingRequest;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 使用Apache HttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheSmartHttpClient extends ApacheHttpClient implements SmartHttpClient , SmartHttpTemplate<HttpEntityEnclosingRequest> {

    @Override
    public ApacheSmartHttpClient setConfig(Config config) {
        super.setConfig(config);
        return this;
    }

    @Override
    public <R> R template(Request request, Method method , ContentCallback<HttpEntityEnclosingRequest> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        //1.获取完整的URL
        String completedUrl = addBaseUrlIfNecessary(request.getUrl());

        HttpUriRequest httpUriRequest = createHttpUriRequest(completedUrl, method);

        //2.设置请求头
        setRequestHeaders(httpUriRequest, request.getContentType(), mergeDefaultHeaders(request.getHeaders()));

        //3.设置请求参数
        setRequestProperty((HttpRequestBase) httpUriRequest,
                getConnectionTimeoutWithDefault(request.getConnectionTimeout()),
                getReadTimeoutWithDefault(request.getReadTimeout()),
                request.getProxyInfo());

        //4.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            if(contentCallback != null){
                contentCallback.doWriteWith((HttpEntityEnclosingRequest)httpUriRequest);
            }
        }

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            ////////////////////////////////////ssl处理///////////////////////////////////
            HostnameVerifier hostnameVerifier = null;
            SSLContext sslContext = null;
            //https默认设置这些
            if(ParamUtil.isHttps(completedUrl)){
                hostnameVerifier = RequestSSLUtil.getHostnameVerifier(request , getHostnameVerifier());
                sslContext = RequestSSLUtil.getSSLContext(request , getSSLContext());
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            httpClient = getCloseableHttpClient(completedUrl , hostnameVerifier , sslContext);
            //6.发送请求
            response = httpClient.execute(httpUriRequest  , HttpClientContext.create());
            int statusCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();

            InputStream inputStream = getStreamFrom(entity , request.isIgnoreResponseBody());

            R convert = resultCallback.convert(statusCode , inputStream, getResultCharsetWithDefault(request.getResultCharset()), parseHeaders(response , request.isIncludeHeaders()));

            IoUtil.close(inputStream);

            return convert;

            ///
            /*Response convert = Response.with(statusCode , inputStream , request.getResultCharset() , request.isIncludeHeaders() ? parseHeaders(response) : new HashMap<>(0));
            IoUtil.close(inputStream);
            return convert;*/
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
    public Response get(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(ParamUtil.contactUrlParams(request.getUrl(), request.getParams() , request.getBodyCharset()), Method.GET,
                request.getContentType(), null, request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(),
                request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request.setUrl(ParamUtil.contactUrlParams(request.getUrl(), request.getParams(), getBodyCharsetWithDefault(request.getBodyCharset()))) ,
                Method.GET , null , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response post(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(),
                r -> setRequestBody(r, request.getBodyIfNullWithParams(), request.getBodyCharset()), request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request, Method.POST ,
                r -> setRequestBody(r, request.getBodyIfNullWithParams(), getBodyCharsetWithDefault(request.getBodyCharset())) , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response httpMethod(Request req, Method method) throws IOException {
        Request request = beforeTemplate(req);
        ContentCallback<HttpEntityEnclosingRequest> contentCallback = null;
        if(method.hasContent()){
            contentCallback = r -> setRequestBody(r, request.getBodyIfNullWithParams(), getBodyCharsetWithDefault(request.getBodyCharset()));
        }
        Response response = template(request, method , contentCallback, Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public byte[] getAsBytes(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(), Method.GET, request.getContentType(),null, request.getHeaders(),
                request.getConnectionTimeout(),request.getReadTimeout() , request.getResultCharset(),request.isIncludeHeaders(),
                (s,b,r,h)-> IoUtil.stream2Bytes(b));*/
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(), Method.GET, request.getContentType(),null, request.getHeaders() ,
                request.getConnectionTimeout(),request.getReadTimeout() , request.getResultCharset(),request.isIncludeHeaders(),
                (s,b,r,h)-> IoUtil.copy2File(b, request.getFile()));*/
        return template(request , Method.GET, null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(), r -> addFormFiles(r, request.getFormFiles()),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        /*使用更全的 ，支持文件和参数一起上传 */

        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(), r -> addFormFiles(r, request.getParams() , request.getFormFiles()),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request , Method.POST ,
                r -> addFormFiles(r, request.getParams(), request.getFormFiles()) , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response afterTemplate(Request request, Response response) throws IOException{
        if(request.isRedirectable() && response.needRedirect()){
            return get(Request.of(response.getRedirectUrl()));
        }
        return response;
    }
}
