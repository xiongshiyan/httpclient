package top.jfunc.common.http.util;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.CharsetUtil;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

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

import static top.jfunc.common.http.HttpConstants.COLON;
import static top.jfunc.common.http.HttpConstants.SPLASH;

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

    public static HttpUriRequest createHttpUriRequest(String url, Method method) {
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

    /**
     * https://ss.xx.xx.ss:8080/dsda
     */
    public static HttpClientBuilder getCloseableHttpClientBuilder(String url, HostnameVerifier hostnameVerifier , SSLContext sslContext , boolean redirectable) throws IOException{
        return createHttpClient(200, 40, 100, url , hostnameVerifier , sslContext , redirectable);
    }

    public static HttpClientBuilder createHttpClient(int maxTotal, int maxPerRoute, int maxRoute, String url , HostnameVerifier hostnameVerifier , SSLContext sslContext , boolean redirectable) throws IOException{
        String hostname = url.split(SPLASH)[2];
        boolean isHttps = ParamUtil.isHttps(url);
        int port = isHttps ? 443 : 80;
        if (hostname.contains(COLON)) {
            String[] arr = hostname.split(COLON);
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }

        ConnectionSocketFactory csf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register(HttpConstants.HTTP, csf)
                .register(HttpConstants.HTTPS, sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        // 设置不活动的连接1000ms之后Validate
        cm.setValidateAfterInactivity(1000);

        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = ApacheUtil::retryIf ;

        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler);

        //是否重定向
        if(!redirectable){
            httpClientBuilder.disableRedirectHandling();
        }

        if(isHttps){
            initSSL(httpClientBuilder , hostnameVerifier , sslContext);
        }
        return httpClientBuilder;
    }

    private static boolean retryIf(IOException exception, int executionCount, HttpContext context) {
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
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#getSSLContext()
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#getSSLContext(String, String)
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
        if(body == null){return;}

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

        if(null != params){
            ///params.keySet().forEach(key->params.get(key).forEach(value->multipartEntityBuilder.addTextBody(key , value)));
            params.forEachKeyValue(multipartEntityBuilder::addTextBody);
        }

        if(null != formFiles){
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

    public static void setRequestHeaders(HttpUriRequest request, String contentType,
                                     MultiValueMap<String, String> headers) {
        //add方式处理多值header
        if(null != headers && !headers.isEmpty()) {
            ///
            /*Set<String> keySet = headers.keySet();
            keySet.forEach((k)->headers.get(k).forEach((v)->request.addHeader(k,v)));*/
            /*Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
            entries.forEach(entry -> entry.getValue().forEach(v->request.addHeader(entry.getKey() , v)));*/
            headers.forEachKeyValue(request::addHeader);
        }

        ///set方式处理单值header
        /*if(null != overwriteHeaders && !overwriteHeaders.isEmpty()){
            overwriteHeaders.forEach(request::setHeader);
        }*/

        if(null != contentType){
            request.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
        }
    }

    public static MultiValueMap<String , String> parseHeaders(HttpResponse response) {
        Header[] allHeaders = response.getAllHeaders();
        MultiValueMap<String,String> arrayListMultimap = new ArrayListMultiValueMap<>(allHeaders.length);
        for (Header header : allHeaders) {
            arrayListMultimap.add(header.getName() , header.getValue());
        }
        return arrayListMultimap;
    }
}
