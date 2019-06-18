package top.jfunc.common.http.basic;

import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import top.jfunc.common.http.HeaderRegular;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.*;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 使用OkHttp3实现的Http请求类
 * @author xiongshiyan at 2018/1/11
 */
public class OkHttp3Client extends AbstractConfigurableHttp implements HttpTemplate<Request.Builder>, HttpClient {

    @Override
    public  <R> R template(String url, Method method , String contentType , ContentCallback<Request.Builder> contentCallback , MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , boolean includeHeaders , ResultCallback<R> resultCallback) throws IOException{
        Objects.requireNonNull(url);
        Response response = null;
        InputStream inputStream = null;
        try {
            String completedUrl = addBaseUrlIfNecessary(url);
            //1.构造OkHttpClient
            OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                    .connectTimeout(getConnectionTimeoutWithDefault(connectTimeout), TimeUnit.MILLISECONDS)
                    .readTimeout(getReadTimeoutWithDefault(readTimeout), TimeUnit.MILLISECONDS);

            ////////////////////////////////////ssl处理///////////////////////////////////
            if(ParamUtil.isHttps(completedUrl)){
                //默认设置这些
                initSSL(clientBuilder , getHostnameVerifier() , getSSLSocketFactory() , getX509TrustManager());
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            //给子类复写的机会
            doWithBuilder(clientBuilder , ParamUtil.isHttps(completedUrl));

            OkHttpClient client = clientBuilder.build();

            doWithClient(client);

            //2.1设置URL
            Request.Builder builder = new Request.Builder().url(completedUrl);

            //2.2处理请求体
            if(null != contentCallback && method.hasContent()){
                contentCallback.doWriteWith(builder);
            }

            //2.3设置headers
            setRequestHeaders(builder , contentType , mergeDefaultHeaders(headers));

            //3.构造请求
            Request request = builder.build();

            //4.执行请求
            response = client.newCall(request).execute();

            //5.获取响应
            inputStream = getStreamFrom(response , false);

            return resultCallback.convert(response.code() , inputStream, getResultCharsetWithDefault(resultCharset), parseHeaders(response , includeHeaders));
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(response);
        }
    }

    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException {
        return template(ParamUtil.contactUrlParams(url , params , getDefaultBodyCharset()), Method.GET,null,null,
                ArrayListMultiValueMap.fromMap(headers),
                connectTimeout,readTimeout,resultCharset,false,(s,b,r,h)-> IoUtil.read(b ,r));
    }

    /**
     * @param bodyCharset 请求体的编码，不支持，需要在contentType中指定
     */
    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url, Method.POST, contentType, d->setRequestBody(d , Method.POST , stringBody(body , contentType)) ,
                ArrayListMultiValueMap.fromMap(headers),
                connectTimeout,readTimeout,resultCharset,false,
                (s,b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public byte[] getAsBytes(String url, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET,null,null, headers ,
                connectTimeout,readTimeout,null,false,
                (s,b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, MultiValueMap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET,null,null, headers ,
                connectTimeout,readTimeout,null,false,
                (s,b,r,h)-> IoUtil.copy2File(b, file));
    }

    @Override
    public String upload(String url, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException {
        MultipartBody requestBody = filesBody(null , files);

        return template(url, Method.POST, null , d->setRequestBody(d, Method.POST , requestBody), headers,
                connectTimeout,readTimeout,resultCharset,false,
                (s,b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , FormFile... files) throws IOException {
        MultipartBody requestBody = filesBody(params , files);

        return template(url, Method.POST, null , d->setRequestBody(d, Method.POST , requestBody), headers,
                connectTimeout,readTimeout,resultCharset,false,
                (s,b,r,h)-> IoUtil.read(b ,r));
    }

    protected InputStream getStreamFrom(Response response , boolean ignoreResponseBody) {
        //忽略返回body的情况下，直接返回空的
        if(ignoreResponseBody){
            return emptyInputStream();
        }
        ResponseBody body = response.body();
        InputStream inputStream = (body != null) ? body.byteStream() : emptyInputStream();
        if(null == inputStream){
            inputStream = emptyInputStream();
        }
        return inputStream;
    }

    protected void doWithBuilder(OkHttpClient.Builder builder , boolean isHttps) throws Exception{
        //default do nothing, give children a chance to do more config
    }
    protected void doWithClient(OkHttpClient okHttpClient) throws Exception{
        //default do nothing, give children a chance to do more config
    }

    /**
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build()
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build(String, String)
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
        MediaType mediaType = null;
        if(null != contentType){
            mediaType = MediaType.parse(contentType);
        }
        return RequestBody.create(mediaType, body);
    }

    /**
     * 文件上传body
     * @param params 伴随文件上传的参数key=value，可以为空
     * @param files 上传文件信息
     */
    protected MultipartBody filesBody(MultiValueMap<String, String> params , FormFile... files) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if(null != params){
            /*params.keySet().forEach(key->params.get(key).forEach(value->builder.addFormDataPart(key , value)));*/
            params.forEachKeyValue(builder::addFormDataPart);
        }

        for (FormFile formFile : files) {
            builder.addPart(Headers.of(HeaderRegular.CONTENT_DISPOSITION.toString(), "form-data; name=\"" + formFile.getParameterName() + "\"; filename=\"" + formFile.getFilName() + "\"") ,
                    inputStreamBody(formFile.getContentType() , formFile.getInStream() , formFile.getFileLen()));
        }
        return builder.build();
    }

    protected RequestBody inputStreamBody(String contentType , InputStream inputStream , long length){
        return new InputStreamRequestBody(contentType , inputStream , length);
    }

    protected void setRequestHeaders(Request.Builder builder, String contentType, MultiValueMap<String, String> headers) {
        if(null != headers && !headers.isEmpty()) {
            ///
            /*Set<String> keySet = headers.keySet();
            keySet.forEach((k)->headers.get(k).forEach((v)->builder.addHeader(k,v)));*/
            headers.forEachKeyValue(builder::addHeader);
        }
        if(null != contentType){
            builder.header(HeaderRegular.CONTENT_TYPE.toString(), contentType);
        }
    }

    /**
     * 获取响应中的headers
     */
    protected MultiValueMap<String , String> parseHeaders(Response response , boolean includeHeaders) {
        if(!includeHeaders){
            return new ArrayListMultiValueMap<>(0);
        }
        Headers resHeaders = response.headers();
        MultiValueMap<String , String> headers = new ArrayListMultiValueMap<>(resHeaders.size());
        Map<String, List<String>> stringListMap = resHeaders.toMultimap();
        stringListMap.forEach((k,l)->l.forEach(v->headers.add(k,v)));
        return headers;
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

    @Override
    public String toString() {
        return "HttpClient implemented by square's OkHttp3";
    }
}
