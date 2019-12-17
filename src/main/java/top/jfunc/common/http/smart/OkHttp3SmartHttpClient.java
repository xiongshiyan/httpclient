package top.jfunc.common.http.smart;

import okhttp3.*;
import okhttp3.Request;
import okhttp3.Response;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.OkHttp3Util;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static top.jfunc.common.http.util.OkHttp3Util.*;

/**
 * 使用OkHttp3 实现的Http请求类
 * @author xiongshiyan at 2018/1/11
 */
public class OkHttp3SmartHttpClient extends AbstractImplementSmartHttpClient<Request.Builder> {

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, Method method , ContentCallback<Request.Builder> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        Response response = null;
        InputStream inputStream = null;
        try {
            /// ParamHolder queryParamHolder = httpRequest.queryParamHolder();
            /// RouteParamHolder routeParamHolder = httpRequest.routeParamHolder();
            /// String completedUrl = handleUrlIfNecessary(httpRequest.getUrl() , routeParamHolder.getMap() , queryParamHolder.getParams() , queryParamHolder.getParamCharset());
            String completedUrl = handleUrlIfNecessary(httpRequest.getUrl());
            //1.创建并配置builder
            OkHttpClient.Builder clientBuilder = createAndConfigBuilder(httpRequest, completedUrl);

            //给子类复写的机会
            OkHttpClient client = createOkHttpClient(clientBuilder , httpRequest);

            //2.1设置URL
            Request.Builder builder = createRequestBuilder(httpRequest , completedUrl);

            //2.2处理请求体
            if(null != contentCallback && method.hasContent()){
                contentCallback.doWriteWith(builder);
            }

            //2.3设置headers
            configHeaders(builder, httpRequest, completedUrl);

            //3.构造请求
            Request okRequest = builder.build();

            //4.执行请求
            response = client.newCall(okRequest).execute();

            //5.获取响应
            inputStream = getStreamFrom(response , httpRequest);

            //6.处理header，包括Cookie的处理
            MultiValueMap<String, String> responseHeaders = determineHeaders(response, httpRequest, completedUrl);

            return resultCallback.convert(response.code(), inputStream,
                    getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    responseHeaders);
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(response);
        }
    }

    protected InputStream getStreamFrom(Response response , HttpRequest httpRequest){
        return OkHttp3Util.getStreamFrom(response, httpRequest.isIgnoreResponseBody());
    }

    @Override
    protected MultiValueMap<String, String> parseResponseHeaders(Object source, HttpRequest httpRequest) {
        return OkHttp3Util.parseHeaders((Response)source , httpRequest.isIncludeHeaders());
    }

    protected Request.Builder createRequestBuilder(HttpRequest httpRequest , String completedUrl) {
        return new Request.Builder().url(completedUrl);
    }

    @Override
    protected void setRequestHeaders(Object target, HttpRequest httpRequest, MultiValueMap<String, String> handledHeaders) throws IOException {
        OkHttp3Util.setRequestHeaders((Request.Builder)target , httpRequest.getContentType() , handledHeaders);
    }

    protected OkHttpClient.Builder createAndConfigBuilder(HttpRequest httpRequest, String completedUrl) {
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
            initSSL(clientBuilder , httpRequest);
        }
        ////////////////////////////////////ssl处理///////////////////////////////////
        return clientBuilder;
    }

    protected void initSSL(OkHttpClient.Builder clientBuilder , HttpRequest httpRequest){
        OkHttp3Util.initSSL(clientBuilder , getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier()) ,
                getSSLSocketFactoryWithDefault(httpRequest.getSslSocketFactory()) ,
                getX509TrustManagerWithDefault(httpRequest.getX509TrustManager()));
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
    protected ContentCallback<Request.Builder> bodyContentCallback(Method method , String body, String bodyCharset, String contentType) throws IOException {
        RequestBody stringBody = stringBody(body, bodyCharset, contentType);
        return d -> setRequestBody(d, method, stringBody);
    }

    @Override
    protected ContentCallback<Request.Builder> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        MultipartBody filesBody = filesBody(params , formFiles);
        return d -> setRequestBody(d, Method.POST , filesBody);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by square's OkHttp3";
    }
}
