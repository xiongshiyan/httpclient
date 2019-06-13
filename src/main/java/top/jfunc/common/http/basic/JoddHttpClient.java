package top.jfunc.common.http.basic;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.http.ProxyInfo;
import jodd.http.net.SSLSocketHttpConnectionProvider;
import jodd.http.net.SocketHttpConnectionProvider;
import jodd.http.up.Uploadable;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.*;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 使用Jodd-Http 实现的Http请求类
 * @author xiongshiyan at 2019/5/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class JoddHttpClient extends AbstractConfigurableHttp implements HttpTemplate<HttpRequest>, HttpClient {

    @Override
    public <R> R template(String url, Method method, String contentType, ContentCallback<HttpRequest> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, boolean includeHeaders, ResultCallback<R> resultCallback) throws IOException {
        HttpResponse response = null;
        try {
            //1.获取完成的URL，创建请求
            String completedUrl = addBaseUrlIfNecessary(url);
            HttpRequest request = new HttpRequest();
            request.method(method.name());
            request.set(completedUrl);

            //2.超时设置
            request.connectionTimeout(getConnectionTimeoutWithDefault(connectTimeout));
            request.timeout(getReadTimeoutWithDefault(readTimeout));


            //3.SSL处理
            initSSL(request , getHostnameVerifier() , getSSLSocketFactory() , getX509TrustManager() , null);

            //4.处理body
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(request);
            }

            //5.设置header
            setRequestHeaders(request , contentType , mergeDefaultHeaders(headers));

            //6.子类可以复写
            doWithHttpRequest(request);

            //7.真正请求
            response = request.send();

            //8.返回处理
            return resultCallback.convert(response.statusCode() ,
                    getStreamFrom(response , false),
                    getResultCharsetWithDefault(resultCharset) ,
                    parseHeaders(response , includeHeaders));
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            if(null != response){
                response.close();
            }
        }
    }

    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException {
        return template(ParamUtil.contactUrlParams(url , params , getDefaultBodyCharset()), Method.GET, null, null, ArrayListMultiValueMap.fromMap(headers),  connectTimeout, readTimeout,
                resultCharset, false , (s, b, r, h)-> IoUtil.read(b , r));
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url, Method.POST, contentType,
                httpRequest -> {
                    String type = null == contentType ?
                            MediaType.APPLICATIPON_JSON.withCharset(bodyCharset).toString() : contentType;
                    httpRequest.body(body.getBytes(getBodyCharsetWithDefault(bodyCharset)), type);
                },
                ArrayListMultiValueMap.fromMap(headers), connectTimeout, readTimeout, resultCharset, false, (s, b, r, h) -> IoUtil.read(b, r));
    }

    @Override
    public byte[] getAsBytes(String url, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET, null, null, headers,
                connectTimeout, readTimeout, null, false ,
                (s, b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, MultiValueMap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET, null, null, headers,
                connectTimeout, readTimeout, null, false ,
                (s, b,r,h)-> IoUtil.copy2File(b, file));
    }

    @Override
    public String upload(String url, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException {
        return template(url, Method.POST, null, httpRequest -> this.upload0(httpRequest , null , files), headers ,
                connectTimeout, readTimeout, resultCharset, false,
                (s, b, r, h) -> IoUtil.read(b, r));
    }

    @Override
    public String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException {
        return template(url, Method.POST, null, httpRequest -> this.upload0(httpRequest , params , files), headers ,
                connectTimeout, readTimeout, resultCharset, false,
                (s, b, r, h) -> IoUtil.read(b, r));
    }

    protected void setRequestHeaders(HttpRequest httpRequest, String contentType, MultiValueMap<String, String> headers) {
        if(null != headers && !headers.isEmpty()) {
            ///
            /*Set<String> keySet = headers.keySet();
            keySet.forEach((k)->headers.get(k).forEach((v)-> httpRequest.header(k , v)));*/
            headers.forEachKeyValue(httpRequest::header);
        }
        if(null != contentType){
            httpRequest.contentType(contentType);
        }
    }

    protected void doWithHttpRequest(HttpRequest httpRequest){}

    protected MultiValueMap<String , String> parseHeaders(HttpResponse response , boolean isIncludeHeaders) {
        if(!isIncludeHeaders){
            return new ArrayListMultiValueMap<>(0);
        }

        Collection<String> headerNames = response.headerNames();
        MultiValueMap<String,String> arrayListMultimap = new ArrayListMultiValueMap<>(headerNames.size());
        for (String headerName : headerNames) {
            List<String> headers = response.headers(headerName);
            for (String headerValue : headers) {
                arrayListMultimap.add(headerName , headerValue);
            }
        }
        return arrayListMultimap;
    }

    /**
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build()
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build(String, String)
     */
    protected void initSSL(HttpRequest httpRequest, HostnameVerifier hostnameVerifier , SSLSocketFactory sslSocketFactory , X509TrustManager trustManager , top.jfunc.common.http.base.ProxyInfo proxyInfo) {
        SocketHttpConnectionProvider httpConnectionProvider = null;
        if("https".equals(httpRequest.protocol())){
            httpConnectionProvider = new SSLSocketHttpConnectionProvider(getSSLSocketFactory());
        }else {
            httpConnectionProvider = new SocketHttpConnectionProvider();
        }
        if(null != proxyInfo){
            Proxy proxy = proxyInfo.getProxy();
            InetSocketAddress address = (InetSocketAddress) proxy.address();
            if(Proxy.Type.DIRECT.equals(proxy.type())){
                httpConnectionProvider.useProxy(ProxyInfo.directProxy());
            }else if(Proxy.Type.HTTP.equals(proxy.type())){
                httpConnectionProvider.useProxy(ProxyInfo.httpProxy(address.getHostName() , address.getPort() , proxyInfo.getProxyUserName() , proxyInfo.getProxyPassword()));
            }else if(Proxy.Type.SOCKS.equals(proxy.type())){
                httpConnectionProvider.useProxy(ProxyInfo.socks5Proxy(address.getHostName() , address.getPort() , proxyInfo.getProxyUserName() , proxyInfo.getProxyPassword()));
            }
        }

        httpRequest.withConnectionProvider(httpConnectionProvider);
    }

    protected InputStream getStreamFrom(HttpResponse httpResponse , boolean ignoreResponseBody) throws IOException{
        //忽略返回body的情况下，直接返回空的
        if(ignoreResponseBody){
            return emptyInputStream();
        }
        byte[] bodyBytes = httpResponse.bodyBytes();
        if(null == bodyBytes || 0 == bodyBytes.length){
            return emptyInputStream();
        }
        return new ByteArrayInputStream(bodyBytes);
    }

    protected void upload0(HttpRequest httpRequest , MultiValueMap<String, String> params,FormFile...formFiles){
        httpRequest.multipart(true);
        if(null != params){
            /*params.getMap().forEach((k , l) ->l.forEach(v->httpRequest.form(k , v)));*/
            params.forEachKeyValue(httpRequest::form);
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

    @Override
    public String toString() {
        return "HttpClient implemented by Jodd-http";
    }
}
