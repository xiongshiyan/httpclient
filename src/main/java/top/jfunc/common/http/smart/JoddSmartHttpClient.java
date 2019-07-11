package top.jfunc.common.http.smart;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.JoddHttpClient;
import top.jfunc.common.http.request.DownloadRequest;
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
public class JoddSmartHttpClient extends JoddHttpClient implements SmartHttpClient, SmartInterceptorHttpTemplate<HttpRequest> {

    @Override
    public <R> R doTemplate(top.jfunc.common.http.request.HttpRequest httpRequest, Method method , ContentCallback<HttpRequest> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        onBeforeIfNecessary(httpRequest, method);

        HttpResponse response = null;
        try {
            //1.获取完成的URL，创建请求
            /// ParamHolder queryParamHolder = httpRequest.queryParamHolder();
            /// RouteParamHolder routeParamHolder = httpRequest.routeParamHolder();
            /// String completedUrl = handleUrlIfNecessary(httpRequest.getUrl() , routeParamHolder.getMap() , queryParamHolder.getParams() , queryParamHolder.getParamCharset());
            String completedUrl = handleUrlIfNecessary(httpRequest.getUrl());

            HttpRequest request = new HttpRequest();
            request.method(method.name());
            request.set(completedUrl);


            //2.超时设置
            request.connectionTimeout(getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()));
            request.timeout(getReadTimeoutWithDefault(httpRequest.getReadTimeout()));

            //3.SSL设置
            initSSL(request , getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier()) ,
                    getSSLSocketFactoryWithDefault(httpRequest.getSslSocketFactory()) ,
                    getX509TrustManagerWithDefault(httpRequest.getX509TrustManager()),
                    getProxyInfoWithDefault(httpRequest.getProxyInfo()));


            //4.处理body
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(request);
            }

            //5.设置header
            MultiValueMap<String, String> headers = mergeDefaultHeaders(httpRequest.getHeaders());

            headers = handleCookieIfNecessary(completedUrl, headers);

            setRequestHeaders(request , httpRequest.getContentType() , headers ,
                    httpRequest.getOverwriteHeaders());

            //6.子类可以复写
            doWithHttpRequest(request , httpRequest);

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

    /**
     * 留给子类做更多的配置
     * @param joddHttpRequest jodd的请求
     * @param httpRequest 请求参数
     */
    protected void doWithHttpRequest(HttpRequest joddHttpRequest , top.jfunc.common.http.request.HttpRequest httpRequest){}

    @Override
    public Response get(top.jfunc.common.http.request.HttpRequest request) throws IOException {
        return template(request , Method.GET , null , Response::with);
    }

    @Override
    public Response post(StringBodyRequest request) throws IOException {
        final String body = request.getBody();
        final String bodyCharset = calculateBodyCharset(request.getBodyCharset() , request.getContentType());
        String contentType = null == request.getContentType() ?
                MediaType.APPLICATIPON_JSON.withCharset(bodyCharset).toString() : request.getContentType();
        return template(request, Method.POST,
                httpRequest -> httpRequest.bodyText(body , contentType , bodyCharset),
                Response::with);
    }

    @Override
    public <R> R http(top.jfunc.common.http.request.HttpRequest request, Method method, ResultCallback<R> resultCallback) throws IOException {
        ContentCallback<HttpRequest> contentCallback = null;
        if(method.hasContent() && request instanceof StringBodyRequest){
            StringBodyRequest bodyRequest = (StringBodyRequest) request;
            final String body = bodyRequest.getBody();
            final String bodyCharset = calculateBodyCharset(bodyRequest.getBodyCharset() , bodyRequest.getContentType());
            contentCallback = req -> req.bodyText(body , bodyRequest.getContentType() , bodyCharset);
        }
        return template(request, method , contentCallback, resultCallback);
    }

    @Override
    public byte[] getAsBytes(top.jfunc.common.http.request.HttpRequest request) throws IOException {
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File download(DownloadRequest request) throws IOException {
        return template(request , Method.GET, null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(UploadRequest request) throws IOException {
        return template(request , Method.POST ,
                r -> upload0(r, request.getFormParams(), request.getParamCharset() ,request.getFormFiles()),
                Response::with);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by Jodd-Http";
    }
}
