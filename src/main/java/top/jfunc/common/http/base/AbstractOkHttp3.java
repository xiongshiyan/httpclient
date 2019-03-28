package top.jfunc.common.http.base;

import top.jfunc.common.http.Header;
import top.jfunc.common.http.HttpStatus;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder;
import top.jfunc.common.http.smart.SSLRequest;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.IoUtil;
import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xiongshiyan at 2018/6/6
 */
public abstract class AbstractOkHttp3 extends AbstractHttp implements HttpTemplate<Request.Builder> {

    @Override
    public <R> R template(top.jfunc.common.http.smart.Request request, Method method , ContentCallback<Request.Builder> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        Response response = null;
        InputStream inputStream = null;
        try {
            String completedUrl = addBaseUrlIfNecessary(request.getUrl());
            //1.构造OkHttpClient
            OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                    .connectTimeout(request.getConnectionTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(request.getReadTimeout(), TimeUnit.MILLISECONDS);

            ////////////////////////////////////ssl处理///////////////////////////////////
            if(isHttps(completedUrl)){
                //默认设置这些
                initSSL(clientBuilder , getDefaultHostnameVerifier() , getDefaultSSLSocketFactory() , getDefaultX509TrustManager());
                if(request instanceof SSLRequest){
                    //客户给了就用给客户的
                    SSLRequest sslRequest = (SSLRequest) request;
                    initSSL(clientBuilder , sslRequest.getHostnameVerifier(), sslRequest.getSslSocketFactory() , sslRequest.getX509TrustManager());
                }
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            //给子类复写的机会
            doWithBuilder(clientBuilder , isHttps(completedUrl));

            OkHttpClient client = clientBuilder.build();

            //2.1设置URL
            Request.Builder builder = new Request.Builder().url(completedUrl);

            ArrayListMultimap<String, String> headers = request.getHeaders();
            //2.2设置headers
            if(null != headers) {
                Set<String> keySet = headers.keySet();
                keySet.forEach((k)->headers.get(k).forEach((v)->builder.addHeader(k,v)));
            }
            if(null != request.getContentType()){
                builder.addHeader(Header.CONTENT_TYPE.toString() , request.getContentType());
            }

            //2.3处理请求体
            if(null != contentCallback && method.hasContent()){
                contentCallback.doWriteWith(builder);
            }

            //3.构造请求
            Request okRequest = builder.build();

            //4.执行请求
            response = client.newCall(okRequest).execute();

            //5.获取响应
            inputStream = getStreamFrom(response);

            int statusCode = response.code();
            return resultCallback.convert(statusCode , inputStream, request.getResultCharset(), request.isIncludeHeaders() ? parseHeaders(response) : new HashMap<>(0));
            /*return top.jfunc.common.http.smart.Response.with(statusCode , inputStream , request.getResultCharset() ,
                    request.isIncludeHeaders() ? parseHeaders(response) : new HashMap<>(0));*/
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            IoUtil.close(inputStream);
            if(null != response){
                try {
                    response.close();
                } catch (Exception e) {}
            }
        }
    }

    private InputStream getStreamFrom(Response response) {
        ResponseBody body = response.body();
        InputStream inputStream = (body != null) ? body.byteStream() : emptyInputStream();
        if(null == inputStream){
            inputStream = emptyInputStream();
        }
        return inputStream;
    }

    @Override
    public  <R> R template(String url, Method method , String contentType , ContentCallback<Request.Builder> contentCallback , ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset , boolean includeHeaders , ResultCallback<R> resultCallback) throws IOException{
        Objects.requireNonNull(url);
        Response response = null;
        InputStream inputStream = null;
        try {
            String completedUrl = addBaseUrlIfNecessary(url);
            //1.构造OkHttpClient
            OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                    .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS);

            ////////////////////////////////////ssl处理///////////////////////////////////
            if(isHttps(completedUrl)){
                //默认设置这些
                initSSL(clientBuilder , getDefaultHostnameVerifier() , getDefaultSSLSocketFactory() , getDefaultX509TrustManager());
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            //给子类复写的机会
            doWithBuilder(clientBuilder , isHttps(completedUrl));

            OkHttpClient client = clientBuilder.build();

            //2.1设置URL
            Request.Builder builder = new Request.Builder().url(completedUrl);

            //2.2设置headers
            if(null != headers) {
                Set<String> keySet = headers.keySet();
                keySet.forEach((k)->headers.get(k).forEach((v)->builder.addHeader(k,v)));
            }
            if(null != contentType){
                builder.addHeader(Header.CONTENT_TYPE.toString() , contentType);
            }

            //2.3处理请求体
            if(null != contentCallback && method.hasContent()){
                contentCallback.doWriteWith(builder);
            }

            //3.构造请求
            Request request = builder.build();

            //4.执行请求
            response = client.newCall(request).execute();

            //5.获取响应
//            return getReturnMsg(response , resultCharset , includeHeaders , resultCallback);
            inputStream = getStreamFrom(response);

            int statusCode = response.code();
            return resultCallback.convert(statusCode , inputStream, resultCharset, includeHeaders ? parseHeaders(response) : new HashMap<>(0));
            ///保留起
            /*if (HttpStatus.HTTP_OK == statusCode) {
                convert = resultCallback.convert(HttpStatus.HTTP_OK , inputStream, resultCharset, includeHeaders ? parseHeaders(response) : new HashMap<>(0));
            }else {
                convert = resultCallback.convert(statusCode , inputStream , resultCharset , parseHeaders(response));
            }
            return convert;*/
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            IoUtil.close(inputStream);
            if(null != response){
                try {
                    response.close();
                } catch (Exception e) {}
            }
        }
    }

    protected void doWithBuilder(OkHttpClient.Builder builder , boolean isHttps) throws Exception{
    }

    /**
     * @see SSLSocketFactoryBuilder#build()
     * @see SSLSocketFactoryBuilder#build(String, String)
     */
    protected void initSSL(OkHttpClient.Builder builder , HostnameVerifier hostnameVerifier , SSLSocketFactory sslSocketFactory , X509TrustManager trustManager) {
        // 验证域
        if(null != hostnameVerifier){
            builder.hostnameVerifier(hostnameVerifier);
        }
        if(null != sslSocketFactory) {
            builder.sslSocketFactory(sslSocketFactory , trustManager);
        }
    }

    protected void setRequestBody(Request.Builder builder , Method method , RequestBody requestBody){
        builder.method(method.name() , requestBody);
    }

    protected RequestBody stringBody(String body , String contentType){
        return RequestBody.create(MediaType.parse(contentType),body);
    }

    protected RequestBody inputStreamBody(String contentType , InputStream inputStream , long length){
        return new InputStreamRequestBody(contentType , inputStream , length);
    }

    protected <R> R getReturnMsg(Response response , String resultCharset , boolean includeHeaders , ResultCallback<R> resultParser) throws IOException {
        int statusCode = response.code();
        /*String body = new String(response.body().bytes() , resultCharset);*/
//        if (HttpStatus.HTTP_OK == statusCode) {
//            InputStream inputStream = response.body().byteStream();
//            R convert = resultParser.convert(inputStream, resultCharset, includeHeaders ? parseHeaders(response) : new HashMap<>(0));
//            IoUtil.close(inputStream);
//            return convert;
//
//        }else {
//            //获取错误body，而不是仅仅状态行信息的message response.message()
//            String errorMsg = new String(response.body().bytes() , resultCharset);
//            throw new HttpException(statusCode,errorMsg,parseHeaders(response));
//        }
        InputStream inputStream = getStreamFrom(response);
        R convert;
        if (HttpStatus.HTTP_OK == statusCode) {
            convert = resultParser.convert(HttpStatus.HTTP_OK , inputStream, resultCharset, includeHeaders ? parseHeaders(response) : new HashMap<>(0));
        }else {
            convert = resultParser.convert(statusCode , inputStream , resultCharset , parseHeaders(response));
        }
        IoUtil.close(inputStream);
        return convert;
    }

    /**
     * 获取响应中的headers
     */
    private Map<String, List<String>> parseHeaders(Response response) {
        Headers resHeaders = response.headers();
        ArrayListMultimap<String , String> headers = new ArrayListMultimap<>(resHeaders.size());
        resHeaders.names().forEach((key)-> headers.put(key,resHeaders.get(key)) );
        return headers.getMap();
    }

    /**
     * 根据inputStream生成requestBody的工具类
     * @see RequestBody#create(MediaType, File)
     */
    private static class InputStreamRequestBody extends RequestBody {
        private String contentType;
        private long len;
        private InputStream stream;

        public InputStreamRequestBody(String contentType, InputStream stream, long len) {
            this.contentType = contentType;
            this.stream = stream;
            this.len = len;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse(contentType);
        }

        @Override
        public long contentLength() throws IOException {
            return len;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            Source source = Okio.source(stream);
            sink.writeAll(source);

            IoUtil.close(stream);
            IoUtil.close(source);
        }
    }
}
