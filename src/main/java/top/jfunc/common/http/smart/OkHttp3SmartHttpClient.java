package top.jfunc.common.http.smart;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.OkHttp3Client;
import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * 使用OkHttp3 实现的Http请求类
 * @author xiongshiyan at 2018/1/11
 */
public class OkHttp3SmartHttpClient extends OkHttp3Client implements SmartHttpClient, SmartInterceptorHttpTemplate<Request.Builder> {

    @Override
    public <R> R doTemplate(HttpRequest httpRequest, Method method , ContentCallback<Request.Builder> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        onBeforeIfNecessary(httpRequest, method);

        okhttp3.Response response = null;
        InputStream inputStream = null;
        try {
            /// ParamHolder queryParamHolder = httpRequest.queryParamHolder();
            /// RouteParamHolder routeParamHolder = httpRequest.routeParamHolder();
            /// String completedUrl = handleUrlIfNecessary(httpRequest.getUrl() , routeParamHolder.getMap() , queryParamHolder.getParams() , queryParamHolder.getParamCharset());
            String completedUrl = handleUrlIfNecessary(httpRequest.getUrl());

            //1.构造OkHttpClient
            OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                    .connectTimeout(getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()), TimeUnit.MILLISECONDS)
                    .readTimeout(getReadTimeoutWithDefault(httpRequest.getReadTimeout()), TimeUnit.MILLISECONDS);
            //1.1如果存在就设置代理
            ProxyInfo proxyInfo = getProxyInfoWithDefault(httpRequest.getProxyInfo());
            if(null != proxyInfo){
                clientBuilder.proxy(proxyInfo.getProxy());
            }

            ////////////////////////////////////ssl处理///////////////////////////////////
            if(ParamUtil.isHttps(completedUrl)){
                initSSL(clientBuilder , getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier()) ,
                        getSSLSocketFactoryWithDefault(httpRequest.getSslSocketFactory()) ,
                        getX509TrustManagerWithDefault(httpRequest.getX509TrustManager()));
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            OkHttpClient client = createOkHttpClient(clientBuilder , httpRequest);

            //2.1设置URL
            Request.Builder builder = new Request.Builder().url(completedUrl);

            //2.2处理请求体
            if(null != contentCallback && method.hasContent()){
                contentCallback.doWriteWith(builder);
            }

            //2.3设置headers
            MultiValueMap<String, String> headers = mergeDefaultHeaders(httpRequest.getHeaders());

            headers = handleCookieIfNecessary(completedUrl, headers);

            setRequestHeaders(builder , httpRequest.getContentType() , headers ,
                    httpRequest.getOverwriteHeaders());

            //3.构造请求
            Request okRequest = builder.build();

            //4.执行请求
            response = client.newCall(okRequest).execute();

            //5.获取响应
            inputStream = getStreamFrom(response , httpRequest.isIgnoreResponseBody());

            //6.处理header，包括Cookie的处理
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

            R convert = resultCallback.convert(response.code(), inputStream,
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
            IoUtil.close(inputStream);
            IoUtil.close(response);
        }
    }

    /**
     * 子类复写，增添更多的功能，保证返回OkHttpClient
     */
    protected OkHttpClient createOkHttpClient(OkHttpClient.Builder builder , HttpRequest httpRequest) throws Exception{
        //默认就使用builder生成
        //可以进一步对builder进行处理
        OkHttpClient okHttpClient = builder.build();
        //对OkHttpClient单独处理
        doWithClient(okHttpClient , httpRequest);
        return okHttpClient;
    }

    /**
     * 子类对ObHttpClient复写
     */
    protected void doWithClient(OkHttpClient okHttpClient , HttpRequest httpRequest) throws Exception{
        //default do nothing, give children a chance to do more config
    }

    @Override
    public Response get(HttpRequest request) throws IOException {
        return template(request , Method.GET , null , Response::with);
    }

    @Override
    public Response post(StringBodyRequest request) throws IOException {
        final String body = request.getBody();
        final String bodyCharset = calculateBodyCharset(request.getBodyCharset() , request.getContentType());
        return template(request, Method.POST ,
                d -> setRequestBody(d, Method.POST, stringBody(body, bodyCharset , request.getContentType())), Response::with);
    }

    @Override
    public <R> R http(HttpRequest request, Method method, ResultCallback<R> resultCallback) throws IOException {
        ContentCallback<Request.Builder> contentCallback = null;
        if(method.hasContent() && request instanceof StringBodyRequest){
            StringBodyRequest bodyRequest = (StringBodyRequest) request;
            final String body = bodyRequest.getBody();
            final String bodyCharset = calculateBodyCharset(bodyRequest.getBodyCharset() , bodyRequest.getContentType());
            contentCallback = d -> setRequestBody(d, method, stringBody(body, bodyCharset , request.getContentType()));
        }
        return template(request, method , contentCallback , resultCallback);
    }

    @Override
    public byte[] getAsBytes(HttpRequest request) throws IOException {
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File download(DownloadRequest request) throws IOException {
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(UploadRequest request) throws IOException {
        MultipartBody requestBody = filesBody(request.getFormParams() , request.getFormFiles());
        return template(request, Method.POST ,
                d -> setRequestBody(d, Method.POST, requestBody) ,
                Response::with);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by square's OkHttp3";
    }
}
