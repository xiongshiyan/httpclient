package cn.zytx.common.http.base;

import cn.zytx.common.http.*;
import cn.zytx.common.http.base.ssl.DefaultTrustManager2;
import cn.zytx.common.http.base.ssl.SSLSocketFactoryBuilder;
import cn.zytx.common.http.base.ssl.TrustAnyHostnameVerifier;
import cn.zytx.common.utils.ArrayListMultimap;
import cn.zytx.common.utils.IoUtil;
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
public abstract class AbstractOkHttp3 implements HttpTemplate<Request.Builder>{
    @Override
    public  <R> R template(String url, Method method , String contentType , ContentCallback<Request.Builder> contentCallback , ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset , boolean includeHeaders , ResultCallback<R> resultCallback) throws IOException{
        Objects.requireNonNull(url);
        Response response = null;
        try {
            //1.构造OkHttpClient
            OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                    .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS);
            //给子类复写的机会
            doWithBuilder(clientBuilder , ParamUtil.isHttps(url));

            OkHttpClient client = clientBuilder.build();

            //2.1设置URL
            Request.Builder builder = new Request.Builder().url(url);

            //2.2设置headers
            if(null != headers) {
                Set<String> keySet = headers.keySet();
                keySet.forEach((k)->headers.get(k).forEach((v)->builder.addHeader(k,v)));
            }
            if(null != contentType){
                builder.addHeader(Header.CONTENT_TYPE.name() , contentType);
            }

            //2.3处理请求体
            if(null != contentCallback && Method.POST == method){
                contentCallback.doWriteWith(builder);
            }

            //3.构造请求
            Request request = builder.build();

            //4.执行请求
            response = client.newCall(request).execute();

            //5.获取响应
            return getReturnMsg(response , resultCharset , includeHeaders , resultCallback);

        } catch (IOException | HttpException e) {
            throw e;
        } catch (Exception e){
            throw new HttpException(e);
        } finally {
            if(null != response){
                try {
                    response.close();
                } catch (Exception e) {}
            }
        }
    }

    protected void doWithBuilder(OkHttpClient.Builder builder , boolean isHttps) throws Exception{
        if(isHttps){
            //默认的ssl处理
            initSSL(builder , new TrustAnyHostnameVerifier() ,
                    SSLSocketFactoryBuilder.create().build() , new DefaultTrustManager2());
        }

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
        if (HttpStatus.HTTP_OK == statusCode) {
            InputStream inputStream = response.body().byteStream();
            R convert = resultParser.convert(inputStream, resultCharset, includeHeaders ? parseHeaders(response) : new HashMap<>(0));
            IoUtil.close(inputStream);
            return convert;

        }else {
            //获取错误body，而不是仅仅状态行信息的message response.message()
            String errorMsg = new String(response.body().bytes() , resultCharset);
            throw new HttpException(statusCode,errorMsg,parseHeaders(response));
        }
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
