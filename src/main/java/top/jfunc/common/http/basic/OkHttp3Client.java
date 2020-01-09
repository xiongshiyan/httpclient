package top.jfunc.common.http.basic;

import okhttp3.*;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.*;
import top.jfunc.common.http.util.OkHttp3Util;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 使用OkHttp3实现的Http请求类
 * @author xiongshiyan at 2018/1/11
 */
public class OkHttp3Client extends AbstractImplementHttpClient<Request.Builder> {

    @Override
    public  <R> R doInternalTemplate(String url, Method method , String contentType , ContentCallback<Request.Builder> contentCallback , MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset , boolean includeHeaders , ResultCallback<R> resultCallback) throws Exception{
        Objects.requireNonNull(url);
        Response response = null;
        InputStream inputStream = null;
        try {
            String completedUrl = handleUrlIfNecessary(url);
            //1.创建并配置builder
            OkHttpClient.Builder clientBuilder = createAndConfigBuilder(completedUrl , connectTimeout , readTimeout);

            //给子类复写的机会
            OkHttpClient client = createOkHttpClient(clientBuilder);

            //2.1设置URL
            Request.Builder builder = createRequestBuilder(completedUrl);

            //2.2处理请求体
            if(null != contentCallback && method.hasContent()){
                contentCallback.doWriteWith(builder);
            }

            //2.3设置headers
            configHeaders(builder, completedUrl , contentType, headers);

            //3.构造请求
            Request request = builder.build();

            //4.执行请求
            response = client.newCall(request).execute();

            //5.获取响应
            inputStream = getStreamFrom(response , false);

            //6.处理header，包括Cookie的处理
            MultiValueMap<String, String> responseHeaders = determineHeaders(response, completedUrl , includeHeaders);

            return resultCallback.convert(response.code() , inputStream,
                    getConfig().getResultCharsetWithDefault(resultCharset),
                    responseHeaders);
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(response);
        }
    }

    protected InputStream getStreamFrom(Response response , boolean ignoreResponseBody){
        return OkHttp3Util.getStreamFrom(response, ignoreResponseBody);
    }

    @Override
    protected MultiValueMap<String, String> parseResponseHeaders(Object source, boolean includeHeaders) {
        return OkHttp3Util.parseHeaders((Response)source , includeHeaders);
    }

    protected Request.Builder createRequestBuilder(String completedUrl) {
        return new Request.Builder().url(completedUrl);
    }

    @Override
    protected void setRequestHeaders(Object target, String contentType, MultiValueMap<String, String> handledHeaders) throws IOException {
        OkHttp3Util.setRequestHeaders((Request.Builder)target , contentType , handledHeaders);
    }

    protected OkHttpClient.Builder createAndConfigBuilder(String completedUrl , int connectionTimeout , int readTimeout) {
        Config config = getConfig();
        //1.构造OkHttpClient
        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(config.getConnectionTimeoutWithDefault(connectionTimeout), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeoutWithDefault(readTimeout), TimeUnit.MILLISECONDS);
        //1.1如果存在就设置代理
        ProxyInfo proxyInfo = config.getDefaultProxyInfo();
        if(null != proxyInfo){
            clientBuilder.proxy(proxyInfo.getProxy());
        }

        ////////////////////////////////////ssl处理///////////////////////////////////
        if(ParamUtil.isHttps(completedUrl)){
            initSSL(clientBuilder);
        }
        ////////////////////////////////////ssl处理///////////////////////////////////
        return clientBuilder;
    }

    protected void initSSL(OkHttpClient.Builder clientBuilder){
        OkHttp3Util.initSSL(clientBuilder , getDefaultHostnameVerifier() ,
                getDefaultSSLSocketFactory() ,
                getDefaultX509TrustManager());
    }
    /**
     * 子类复写，增添更多的功能，保证返回OkHttpClient
     */
    protected OkHttpClient createOkHttpClient(OkHttpClient.Builder builder) throws Exception{
        //默认就使用builder生成
        //可以进一步对builder进行处理
        OkHttpClient okHttpClient = builder.build();
        //对OkHttpClient单独处理
        doWithClient(okHttpClient);
        return okHttpClient;
    }

    /**
     * 子类对ObHttpClient复写
     */
    protected void doWithClient(OkHttpClient okHttpClient) throws Exception{
        //default do nothing, give children a chance to do more config
    }


    @Override
    protected ContentCallback<Request.Builder> bodyContentCallback(Method method , String body, String bodyCharset, String contentType) throws IOException {
        RequestBody stringBody = OkHttp3Util.stringBody(body, bodyCharset, contentType);
        return d -> OkHttp3Util.setRequestBody(d, method, stringBody);
    }

    @Override
    protected ContentCallback<Request.Builder> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        MultipartBody filesBody = OkHttp3Util.filesBody(params , formFiles);
        return d -> OkHttp3Util.setRequestBody(d, Method.POST , filesBody);
    }
    @Override
    public String toString() {
        return "HttpClient implemented by square's OkHttp3";
    }
}
