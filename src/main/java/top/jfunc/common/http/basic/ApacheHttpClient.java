package top.jfunc.common.http.basic;

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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import top.jfunc.common.http.HeaderRegular;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.*;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.IoUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 使用Apache HttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheHttpClient extends AbstractConfigurableHttp implements HttpTemplate<HttpEntityEnclosingRequest> , HttpClient {
    protected int _maxRetryTimes = 1;

    @Override
    public ApacheHttpClient setConfig(Config config) {
        super.setConfig(config);
        return this;
    }

    @Override
    public  <R> R template(String url, Method method , String contentType, ContentCallback<HttpEntityEnclosingRequest> contentCallback, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , boolean includeHeader , ResultCallback<R> resultCallback) throws IOException {
        //1.获取完成的URL，创建请求
        String completedUrl = addBaseUrlIfNecessary(url);
        ///*URIBuilder builder = new URIBuilder(url);
        //URI uri = builder.build();*/
        HttpUriRequest httpUriRequest = createHttpUriRequest(completedUrl , method);

        //2.设置请求头
        setRequestHeaders(httpUriRequest, contentType, mergeDefaultHeaders(headers));

        //3.设置请求参数
        setRequestProperty((HttpRequestBase) httpUriRequest,
                getConnectionTimeoutWithDefault(connectTimeout),
                getReadTimeoutWithDefault(readTimeout));

        //4.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            if(contentCallback != null){
                contentCallback.doWriteWith((HttpEntityEnclosingRequest)httpUriRequest);
            }
        }

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {

            //5.创建http客户端
            ///CloseableHttpClient httpClient = HttpClients.createDefault();
            httpClient = getCloseableHttpClient(completedUrl ,getHostnameVerifier() , getSSLContext());

            //6.发送请求
            response = httpClient.execute(httpUriRequest  , HttpClientContext.create());
            int statusCode = response.getStatusLine().getStatusCode();
            /*String resultString = EntityUtils.toString(response.getEntity(), resultCharset);*/

            entity = response.getEntity();

//            if (HttpStatus.HTTP_OK == statusCode) {
//                //7.封装返回参数
//                InputStream inputStream = entity.getContent();
//                R convert = resultCallback.convert(inputStream, resultCharset, includeHeader ? parseHeaders(response) : new HashMap<>(0));
//                IoUtil.close(inputStream);
//                return convert;
//            }else {
//                String errorMsg = EntityUtils.toString(entity, resultCharset);
//                throw new HttpException(statusCode,errorMsg,parseHeaders(response));
//            }
            //InputStream inputStream = entity.getContent();
//            R convert;
//            if (HttpStatus.HTTP_OK == statusCode) {
//                //7.封装返回参数
//                convert = resultCallback.convert(HttpStatus.HTTP_OK , inputStream, resultCharset, includeHeader ? parseHeaders(response) : new HashMap<>(0));
//            }else {
//                convert = resultCallback.convert(statusCode , inputStream , resultCharset , parseHeaders(response));
//            }
            /*if(null == inputStream){
                inputStream = top.jfunc.common.http.IoUtil.emptyInputStream();
            }*/

            InputStream inputStream = getStreamFrom(entity, false);


            R convert = resultCallback.convert(statusCode , inputStream, getResultCharsetWithDefault(resultCharset),  parseHeaders(response , includeHeader));
            IoUtil.close(inputStream);
            return convert;
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            EntityUtils.consumeQuietly(entity);
            IoUtil.close(response);
            IoUtil.close(httpClient);
        }
    }

    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException{
        return template(ParamUtil.contactUrlParams(url , params , getDefaultBodyCharset()), Method.GET,null,null,
                ArrayListMultimap.fromMap(headers),
                connectTimeout,readTimeout , resultCharset,false,(s, b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url,Method.POST, contentType, (request -> setRequestBody(request , body ,getBodyCharsetWithDefault(bodyCharset))),
                ArrayListMultimap.fromMap(headers),
                connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET,null,null, headers,
                connectTimeout,readTimeout , null,false,
                (s, b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, ArrayListMultimap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET,null,null, headers ,
                connectTimeout,readTimeout , null,false,
                (s, b,r,h)-> IoUtil.copy2File(b, file));
    }


    @Override
    public String upload(String url, ArrayListMultimap<String,String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException{
        return template(url,Method.POST, null, (request -> addFormFiles(request, null , files)),
                headers, connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException {
        return template(url,Method.POST, null, (request -> addFormFiles(request, params ,files)),
                headers, connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }




    protected InputStream getStreamFrom(HttpEntity entity , boolean ignoreResponseBody) throws IOException {
        //忽略返回body的情况下，直接返回空的
        if(ignoreResponseBody){
            return top.jfunc.common.http.IoUtil.emptyInputStream();
        }
        InputStream inputStream = entity.getContent();
        if(null == inputStream){
            inputStream = top.jfunc.common.http.IoUtil.emptyInputStream();
        }
        return inputStream;
    }

    protected HttpUriRequest createHttpUriRequest(String url, Method method) {
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
    protected CloseableHttpClient getCloseableHttpClient(String url, HostnameVerifier hostnameVerifier , SSLContext sslContext) throws Exception{
        return createHttpClient(200, 40, 100, url , hostnameVerifier , sslContext);
    }

    private boolean retryIf(IOException exception, int executionCount, HttpContext context) {
        if (executionCount >= _maxRetryTimes) {
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

    protected CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute, int maxRoute, String url , HostnameVerifier hostnameVerifier , SSLContext sslContext) throws Exception{
        String hostname = url.split("/")[2];
        boolean isHttps = ParamUtil.isHttps(url);
        int port = isHttps ? 443 : 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }

        ConnectionSocketFactory csf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", csf)
                .register("https", sslsf).build();
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
        HttpRequestRetryHandler httpRequestRetryHandler = this::retryIf ;

        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler);

        if(isHttps){
            initSSL(httpClientBuilder , hostnameVerifier , sslContext);
        }

        //给子类复写的机会
        doWithClient(httpClientBuilder , isHttps);

        return httpClientBuilder.build();
    }

    protected void doWithClient(HttpClientBuilder httpClientBuilder , boolean isHttps) throws Exception{
        //default do nothing, give children a chance to do more config
    }

    /**
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#getSSLContext()
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#getSSLContext(String, String)
     */
    protected void initSSL(HttpClientBuilder httpClientBuilder , HostnameVerifier hostnameVerifier , SSLContext sslContext) {
        // 验证域
        if(null != hostnameVerifier){
            httpClientBuilder.setSSLHostnameVerifier(hostnameVerifier);
        }
        if(null != sslContext){
            httpClientBuilder.setSSLContext(sslContext);
        }
    }

    protected void setRequestBody(HttpEntityEnclosingRequest request, String body, String bodyCharset) {
        if(body == null || bodyCharset == null){return;}

        StringEntity entity = new StringEntity(body, bodyCharset);
        entity.setContentEncoding(bodyCharset);
        request.setEntity(entity);
    }


    protected void addFormFiles(HttpEntityEnclosingRequest request, ArrayListMultimap<String, String> params ,FormFile[] files) throws UnsupportedEncodingException {
        final MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(CharsetUtils.get(getDefaultBodyCharset()));

        if(null != params){
            params.keySet().forEach(key->params.get(key).forEach(value->multipartEntityBuilder.addTextBody(key , value)));
        }

        for (FormFile formFile : files) {
            multipartEntityBuilder.addBinaryBody(formFile.getParameterName(), formFile.getInStream() , ContentType.parse(formFile.getContentType()) , formFile.getFilName());
        }
        HttpEntity reqEntity = multipartEntityBuilder.build();
        request.setEntity(reqEntity);
    }


    protected void setRequestProperty(HttpRequestBase request,
                                      int connectTimeout,
                                      int readTimeout) {
        setRequestProperty(request, connectTimeout, readTimeout , null);
    }
    protected void setRequestProperty(HttpRequestBase request,
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

    protected void setRequestHeaders(HttpUriRequest request, String contentType, ArrayListMultimap<String, String> headers) {
        if(null != headers) {
            Set<String> keySet = headers.keySet();
            keySet.forEach((k)->headers.get(k).forEach((v)->request.addHeader(k,v)));
        }
        if(null != contentType){
            request.setHeader(HeaderRegular.CONTENT_TYPE.toString(), contentType);
        }
    }

    protected Map<String , List<String>> parseHeaders(CloseableHttpResponse response, boolean isIncludeHeaders) {
        if(!isIncludeHeaders){
            return new HashMap<>(0);
        }
        Header[] allHeaders = response.getAllHeaders();
        ArrayListMultimap<String,String> arrayListMultimap = new ArrayListMultimap<>(allHeaders.length);
        for (Header header : allHeaders) {
            arrayListMultimap.put(header.getName() , header.getValue());
        }
        return arrayListMultimap.getMap();
    }
}
