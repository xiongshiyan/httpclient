package top.jfunc.common.http.util;

import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.http.base.Method;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.utils.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;

/**
 * @author xiongshiyan at 2019/7/9 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ApacheUtil {
    public static InputStream getStreamFrom(HttpEntity entity) throws IOException {
        InputStream inputStream = entity.getContent();
        if(null == inputStream){
            inputStream = IoUtil.emptyStream();
        }
        return inputStream;
    }

    public static HttpRequest createHttpUriRequest(String url, Method method) {
        switch (method){
            case GET     : return new HttpGet(url);
            case POST    : return new HttpPost(url);
            case PUT     : return new HttpPut(url);
            case OPTIONS : return new HttpOptions(url);
            case HEAD    : return new HttpHead(url);
            case PATCH   : return new HttpPatch(url);
            case TRACE   : return new HttpTrace(url);
            case DELETE  : return new HttpDelete(url);
            default      : throw new IllegalArgumentException("不支持的http方法 : " + method.name());
        }
    }

    public static boolean retryIf(IOException exception, int executionCount, HttpContext context) {
        if (executionCount >= 3) {
            return false;
        }
        // 如果服务器丢掉了连接，那么就重试
        if (exception instanceof NoHttpResponseException) {
            return true;
        }
        // 不要重试SSL握手异常
        if (exception instanceof SSLHandshakeException) {
            return false;
        }
        // 超时
        if (exception instanceof InterruptedIOException) {
            return false;
        }
        // 目标服务器不可达
        if (exception instanceof UnknownHostException) {
            return false;
        }
        // 连接被拒绝
        if (exception instanceof ConnectException) {
            return false;
        }
        // SSL握手异常
        if (exception instanceof SSLException) {
            return false;
        }

        HttpClientContext clientContext = HttpClientContext
                .adapt(context);
        HttpRequest request = clientContext.getRequest();
        // 如果请求是幂等的，就再次尝试
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            return true;
        }
        return false;
    }

    /**
     * @see top.jfunc.common.http.ssl.SSLSocketFactoryBuilder#getSSLContext()
     * @see top.jfunc.common.http.ssl.SSLSocketFactoryBuilder#getSSLContext(String, String)
     */
    public static void initSSL(HttpClientBuilder httpClientBuilder , HostnameVerifier hostnameVerifier , SSLContext sslContext) {
        // 验证域
        if(null != hostnameVerifier){
            httpClientBuilder.setSSLHostnameVerifier(hostnameVerifier);
        }
        if(null != sslContext){
            httpClientBuilder.setSSLContext(sslContext);
        }
    }

    public static void setRequestBody(HttpEntityEnclosingRequest request, String body, String bodyCharset) {
        StringEntity entity = new StringEntity(body, bodyCharset);
        entity.setContentEncoding(bodyCharset);
        request.setEntity(entity);
    }


    /**
     * 给Request中添加key-value和上传文件信息
     * @param request HttpEntityEnclosingRequest
     * @param params Key-Value参数
     * @param charset 编码
     * @param formFiles 文件上传信息
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    public static void upload0(HttpEntityEnclosingRequest request, MultiValueMap<String, String> params , String charset , Iterable<FormFile> formFiles) throws UnsupportedEncodingException {
        final MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(CharsetUtil.charset(charset));

        if(MapUtil.notEmpty(params)){
            params.forEachKeyValue(multipartEntityBuilder::addTextBody);
        }

        if(ArrayUtil.isNotEmpty(formFiles)){
            for (FormFile formFile : formFiles) {
                multipartEntityBuilder.addBinaryBody(formFile.getParameterName(), formFile.getInStream() , ContentType.parse(formFile.getContentType()) , formFile.getFilName());
            }
        }

        HttpEntity reqEntity = multipartEntityBuilder.build();
        request.setEntity(reqEntity);
    }


    public static void setRequestProperty(HttpRequestBase request,
                                      int connectTimeout,
                                      int readTimeout) {
        setRequestProperty(request, connectTimeout, readTimeout , null);
    }
    public static void setRequestProperty(HttpRequestBase request,
                                      int connectTimeout,
                                      int readTimeout,
                                      ProxyInfo proxyInfo) {
        RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(readTimeout)
                .setSocketTimeout(readTimeout)
                //.setStaleConnectionCheckEnabled(true)
                ;
        //代理设置
        if(null != proxyInfo){
            Proxy proxy = proxyInfo.getProxy();
            InetSocketAddress address = (InetSocketAddress)proxy.address();
            HttpHost proxyHost = new HttpHost(address.getHostName() , address.getPort());
            builder.setProxy(proxyHost);
        }
        request.setConfig(builder.build());
    }

    public static void setRequestHeaders(HttpRequest request, String contentType,
                                     MultiValueMap<String, String> headers) {
        if(MapUtil.notEmpty(headers)) {
            headers.forEachKeyValue(request::addHeader);
        }

        if(null != contentType){
            request.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
        }
    }

    public static MultiValueMap<String , String> parseHeaders(HttpResponse response) {
        Header[] allHeaders = response.getAllHeaders();
        MultiValueMap<String,String> multiValueMap = new ArrayListMultiValueMap<>(allHeaders.length);
        for (Header header : allHeaders) {
            multiValueMap.add(header.getName() , header.getValue());
        }
        return multiValueMap;
    }


    public static void closeQuietly(HttpResponse httpResponse) {
        if(null != httpResponse && httpResponse instanceof CloseableHttpResponse){
            try{
                ((CloseableHttpResponse) httpResponse).close();
            }catch (Exception e){}
        }
    }
}
