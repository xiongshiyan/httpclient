package top.jfunc.common.http.smart;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;

import static top.jfunc.common.http.util.JoddUtil.*;

/**
 * 使用Jodd-http 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class JoddSmartHttpClient extends AbstractSmartHttpClient<HttpRequest> {

    @Override
    protected <R> R doInternalTemplate(top.jfunc.common.http.request.HttpRequest httpRequest, Method method , ContentCallback<HttpRequest> contentCallback , ResultCallback<R> resultCallback) throws Exception {
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

            setRequestHeaders(request , httpRequest.getContentType() , headers);

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

            return resultCallback.convert(response.statusCode(),
                    getStreamFrom(response, httpRequest.isIgnoreResponseBody()),
                    getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    parseHeaders);
        } finally {
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
    protected ContentCallback<HttpRequest> bodyContentCallback(Method method , String body, String bodyCharset, String contentType) throws IOException {
        String type = null == contentType ?
                MediaType.APPLICATIPON_JSON.withCharset(bodyCharset).toString() : contentType;
        return httpRequest -> httpRequest.bodyText(body , type, bodyCharset);
    }

    @Override
    protected ContentCallback<HttpRequest> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, FormFile[] formFiles) throws IOException {
        return httpRequest -> upload0(httpRequest , params , paramCharset , formFiles);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by Jodd-Http";
    }
}
