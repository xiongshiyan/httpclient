package top.jfunc.common.http.util;

import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import top.jfunc.common.http.HeaderRegular;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static top.jfunc.common.http.base.StreamUtil.emptyInputStream;

/**
 * @author xiongshiyan at 2019/7/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class OkHttp3Util {
    public static InputStream getStreamFrom(Response response , boolean ignoreResponseBody) {
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

    /**
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build()
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build(String, String)
     */
    public static void initSSL(OkHttpClient.Builder builder , HostnameVerifier hostnameVerifier , SSLSocketFactory sslSocketFactory , X509TrustManager trustManager) {
        // 验证域
        if(null != hostnameVerifier){
            builder.hostnameVerifier(hostnameVerifier);
        }
        if(null != sslSocketFactory) {
            builder.sslSocketFactory(sslSocketFactory , trustManager);
        }
    }

    public static void setRequestBody(Request.Builder builder , Method method , RequestBody requestBody){
        builder.method(method.name() , requestBody);
    }

    public static RequestBody stringBody(String body , String bodyCharset , String contentType){
        MediaType mediaType = null;
        if(null != contentType){
            mediaType = MediaType.parse(contentType);
        }
        return RequestBody.create(mediaType, body.getBytes(Charset.forName(bodyCharset)));
    }

    /**
     * 文件上传body
     * @param params 伴随文件上传的参数key=value，可以为空
     * @param files 上传文件信息
     */
    public static MultipartBody filesBody(MultiValueMap<String, String> params , FormFile... files) {
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

    public static RequestBody inputStreamBody(String contentType , InputStream inputStream , long length){
        return new InputStreamRequestBody(contentType , inputStream , length);
    }

    public static void setRequestHeaders(Request.Builder builder, String contentType,
                                     MultiValueMap<String, String> headers) {
        //add方式处理多值header
        if(null != headers && !headers.isEmpty()) {
            ///
            /*Set<String> keySet = headers.keySet();
            keySet.forEach((k)->headers.get(k).forEach((v)->builder.addHeader(k,v)));*/
            headers.forEachKeyValue(builder::addHeader);
        }

        ///set方式处理单值header
        /*if(null != overwriteHeaders && !overwriteHeaders.isEmpty()){
            overwriteHeaders.forEach(builder::header);
        }*/

        if(null != contentType){
            builder.header(HeaderRegular.CONTENT_TYPE.toString(), contentType);
        }
    }

    /**
     * 获取响应中的headers
     */
    public static MultiValueMap<String , String> parseHeaders(Response response , boolean includeHeaders) {
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
}
