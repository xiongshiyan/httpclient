package top.jfunc.common.http.basic;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.http.ProxyInfo;
import jodd.http.net.SSLSocketHttpConnectionProvider;
import jodd.http.net.SocketHttpConnectionProvider;
import jodd.http.up.Uploadable;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.*;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.IoUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

/**
 * 使用Jodd-Http 实现的Http请求类
 * @author xiongshiyan at 2019/5/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class JoddHttpClient extends AbstractConfigurableHttp implements HttpTemplate<HttpRequest> , HttpClient{

    @Override
    public JoddHttpClient setConfig(Config config) {
        super.setConfig(config);
        return this;
    }

    @Override
    public <R> R template(String url, Method method, String contentType, ContentCallback<HttpRequest> contentCallback, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, boolean includeHeaders, ResultCallback<R> resultCallback) throws IOException {
        //1.获取完成的URL，创建请求
        String completedUrl = addBaseUrlIfNecessary(url);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.method(method.name());
        httpRequest.set(completedUrl);

        //2.设置header
        setRequestHeaders(httpRequest , contentType , mergeDefaultHeaders(headers));

        //3.超时设置
        httpRequest.connectionTimeout(getConnectionTimeoutWithDefault(connectTimeout));
        httpRequest.timeout(getReadTimeoutWithDefault(readTimeout));


        //4.SSL处理
        initSSL(httpRequest , getHostnameVerifier() , getSSLSocketFactory() , getX509TrustManager() , null);

        //5.处理body
        if(contentCallback != null && method.hasContent()){
            contentCallback.doWriteWith(httpRequest);
        }

        //6.子类可以复写
        doWithHttpRequest(httpRequest);

        //7.真正请求
        HttpResponse response = httpRequest.send();

        //8.返回处理
        return resultCallback.convert(response.statusCode() ,
                getStreamFrom(response , false),
                getResultCharsetWithDefault(resultCharset) ,
                includeHeaders ? parseHeaders(response) : new HashMap<>(0));
    }

    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException {
        return template(ParamUtil.contactUrlParams(url , params , getDefaultBodyCharset()), Method.GET, null, null, ArrayListMultimap.fromMap(headers),  connectTimeout, readTimeout,
                resultCharset, false , (s, b, r, h)-> IoUtil.read(b , r));
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url, Method.POST, contentType, httpRequest -> httpRequest.body(body.getBytes(getBodyCharsetWithDefault(bodyCharset)) , contentType),
                ArrayListMultimap.fromMap(headers), connectTimeout, readTimeout, resultCharset, false, (s, b, r, h) -> IoUtil.read(b, r));
    }

    @Override
    public byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET, null, null, headers,
                connectTimeout, readTimeout, null, false ,
                (s, b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, ArrayListMultimap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET, null, null, headers,
                connectTimeout, readTimeout, null, false ,
                (s, b,r,h)-> IoUtil.copy2File(b, file));
    }

    @Override
    public String upload(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException {
        return template(url, Method.POST, null, httpRequest -> this.upload0(httpRequest , null , files), headers ,
                connectTimeout, readTimeout, resultCharset, false,
                (s, b, r, h) -> IoUtil.read(b, r));
    }

    @Override
    public String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException {
        return template(url, Method.POST, null, httpRequest -> this.upload0(httpRequest , params , files), headers ,
                connectTimeout, readTimeout, resultCharset, false,
                (s, b, r, h) -> IoUtil.read(b, r));
    }

    protected void setRequestHeaders(HttpRequest httpRequest, String contentType, ArrayListMultimap<String, String> headers) {
        if(null != headers) {
            Set<String> keySet = headers.keySet();
            keySet.forEach((k)->headers.get(k).forEach((v)-> httpRequest.header(k , v)));
        }
        if(null != contentType){
            httpRequest.contentType(contentType);
        }
    }

    protected void doWithHttpRequest(HttpRequest httpRequest){}

    protected Map<String , List<String>> parseHeaders(HttpResponse response) {
        Collection<String> headerNames = response.headerNames();
        ArrayListMultimap<String,String> arrayListMultimap = new ArrayListMultimap<>(headerNames.size());
        for (String headerName : headerNames) {
            List<String> headers = response.headers(headerName);
            for (String headerValue : headers) {
                arrayListMultimap.put(headerName , headerValue);
            }
        }
        return arrayListMultimap.getMap();
    }

    /**
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build()
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build(String, String)
     */
    protected void initSSL(HttpRequest httpRequest, HostnameVerifier hostnameVerifier , SSLSocketFactory sslSocketFactory , X509TrustManager trustManager , java.net.Proxy proxy) {
        SocketHttpConnectionProvider httpConnectionProvider = null;
        if(ParamUtil.isHttps(httpRequest.url())){
            httpConnectionProvider = new SSLSocketHttpConnectionProvider(getSSLSocketFactory());
        }else {
            httpConnectionProvider = new SocketHttpConnectionProvider();
        }
        if(null != proxy){
            InetSocketAddress address = (InetSocketAddress) proxy.address();
            if(Proxy.Type.DIRECT.equals(proxy.type())){
                httpConnectionProvider.useProxy(ProxyInfo.directProxy());
            }else if(Proxy.Type.HTTP.equals(proxy.type())){
                httpConnectionProvider.useProxy(ProxyInfo.httpProxy(address.getHostName() , address.getPort() , null , null));
            }else if(Proxy.Type.SOCKS.equals(proxy.type())){
                httpConnectionProvider.useProxy(ProxyInfo.socks5Proxy(address.getHostName() , address.getPort() , null , null));
            }
        }

        httpRequest.withConnectionProvider(httpConnectionProvider);
    }

    protected InputStream getStreamFrom(HttpResponse httpResponse , boolean ignoreResponseBody) throws IOException{
        //忽略返回body的情况下，直接返回空的
        if(ignoreResponseBody){
            return top.jfunc.common.http.IoUtil.emptyInputStream();
        }
        return new ByteArrayInputStream(httpResponse.bodyBytes());
    }

    protected void upload0(HttpRequest httpRequest , ArrayListMultimap<String, String> params,FormFile...formFiles){
        httpRequest.multipart(true);
        if(null != params){
            params.getMap().forEach((k , l) ->l.forEach(v->httpRequest.form(k , v)));
        }
        for (FormFile formFile : formFiles) {
            httpRequest.form(formFile.getParameterName() , new FormFileUploadable(formFile));
        }
    }

    protected static class FormFileUploadable implements Uploadable<FormFile>{
        private final FormFile formFile;

        public FormFileUploadable(FormFile formFile){
            this.formFile = formFile;
        }
        @Override
        public FormFile getContent() {
            return this.formFile;
        }

        @Override
        public byte[] getBytes() {
            try {
                return IoUtil.stream2Bytes(this.formFile.getInStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getFileName() {
            return this.formFile.getFilName();
        }

        @Override
        public String getMimeType() {
            return this.formFile.getContentType();
        }

        @Override
        public int getSize() {
            return Long.valueOf(this.formFile.getFileLen()).intValue();
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return this.formFile.getInStream();
        }
    }
}
