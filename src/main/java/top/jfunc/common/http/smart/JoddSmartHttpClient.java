package top.jfunc.common.http.smart;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.JoddHttpClient;
import top.jfunc.common.http.holder.ParamHolder;
import top.jfunc.common.http.holder.RouteParamHolder;
import top.jfunc.common.http.holder.SSLHolder;
import top.jfunc.common.http.request.CharsetUtil;
import top.jfunc.common.http.request.DownLoadRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;

/**
 * 使用Jodd-http 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class JoddSmartHttpClient extends JoddHttpClient implements SmartHttpClient, SmartHttpTemplate<HttpRequest> {

    @Override
    public <R> R template(top.jfunc.common.http.request.HttpRequest httpRequest, Method method , ContentCallback<HttpRequest> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        onBeforeIfNecessary(httpRequest, method);

        HttpResponse response = null;
        try {
            //1.获取完成的URL，创建请求
            ParamHolder queryParamHolder = httpRequest.queryParamHolder();
            RouteParamHolder routeParamHolder = httpRequest.routeParamHolder();
            String completedUrl = handleUrlIfNecessary(httpRequest.getUrl() , routeParamHolder.getRouteParams() , queryParamHolder.getParams() , queryParamHolder.getParamCharset());

            HttpRequest request = new HttpRequest();
            request.method(method.name());
            request.set(completedUrl);


            //2.超时设置
            request.connectionTimeout(getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()));
            request.timeout(getReadTimeoutWithDefault(httpRequest.getReadTimeout()));

            //3.SSL设置
            SSLHolder sslHolder = httpRequest.sslHolder();
            initSSL(request , getHostnameVerifierWithDefault(sslHolder.getHostnameVerifier()) ,
                    getSSLSocketFactoryWithDefault(sslHolder.getSslSocketFactory()) ,
                    getX509TrustManagerWithDefault(sslHolder.getX509TrustManager()),
                    getProxyInfoWithDefault(httpRequest.getProxyInfo()));


            //4.处理body
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(request);
            }

            //5.设置header
            MultiValueMap<String, String> headers = mergeDefaultHeaders(httpRequest.headerHolder().getHeaders());

            headers = handleCookieIfNecessary(completedUrl, headers);

            setRequestHeaders(request , httpRequest.getContentType() , headers);

            //6.子类可以复写
            doWithHttpRequest(request);

            //7.真正请求
            response = request.send();

            //8.返回header,包括Cookie处理
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

            R convert = resultCallback.convert(response.statusCode(),
                    getStreamFrom(response, httpRequest.isIgnoreResponseBody()),
                    getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    parseHeaders);

            onAfterReturnIfNecessary(httpRequest , convert);

            return convert;
        } catch (IOException e) {
            onErrorIfNecessary(httpRequest , e);
            throw e;
        } catch (Exception e){
            onErrorIfNecessary(httpRequest , e);
            throw new RuntimeException(e);
        } finally {
            onAfterIfNecessary(httpRequest);
            if(null != response){
                response.close();
            }
        }
    }

    @Override
    public Response get(top.jfunc.common.http.request.HttpRequest req) throws IOException {
        top.jfunc.common.http.request.HttpRequest request = beforeTemplate(req);
        Response response = template(request , Method.GET , null , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response post(StringBodyRequest req) throws IOException {
        StringBodyRequest request = beforeTemplate(req);
        String body = request.getBody();
        final String bodyCharset = CharsetUtil.bodyCharsetFromRequest(request);
        Response response = template(request, Method.POST,
                httpRequest -> {
                    String bodyCharsetWithDefault = getBodyCharsetWithDefault(bodyCharset);
                    String contentType = null == request.getContentType() ?
                            MediaType.APPLICATIPON_JSON.withCharset(bodyCharsetWithDefault).toString() : request.getContentType();
                    httpRequest.body(body.getBytes(bodyCharsetWithDefault), contentType);
                }, Response::with);

        return afterTemplate(request , response);
    }

    @Override
    public Response httpMethod(top.jfunc.common.http.request.HttpRequest req, Method method) throws IOException {
        top.jfunc.common.http.request.HttpRequest request = beforeTemplate(req);
        ContentCallback<HttpRequest> contentCallback = null;
        if(method.hasContent() && request instanceof StringBodyRequest){
            String body = ((StringBodyRequest)request).getBody();
            final String bodyCharset = CharsetUtil.bodyCharsetFromRequest(request);
            contentCallback = httpRequest -> httpRequest.body(body.getBytes(getBodyCharsetWithDefault(bodyCharset)), request.getContentType());
        }
        Response response = template(request, method , contentCallback, Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public byte[] getAsBytes(top.jfunc.common.http.request.HttpRequest req) throws IOException {
        top.jfunc.common.http.request.HttpRequest request = beforeTemplate(req);
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
                r -> {
                    ParamHolder paramHolder = request.formParamHolder();
                    upload0(r, paramHolder.getParams(), paramHolder.getParamCharset() ,request.getFormFiles());
                }, Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by Jodd-Http";
    }
}
