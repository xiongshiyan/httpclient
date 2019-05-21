package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.NativeHttpClient;
import top.jfunc.common.http.request.DownLoadRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.impl.GetRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeSmartHttpClient extends NativeHttpClient implements SmartHttpClient, SmartHttpTemplate<HttpURLConnection> {

    @Override
    public NativeSmartHttpClient setConfig(Config config) {
        super.setConfig(config);
        return this;
    }

    @Override
    public <R> R template(HttpRequest httpRequest, Method method, ContentCallback<HttpURLConnection> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //1.获取连接
            String completedUrl = handleUrlIfNecessary(httpRequest.getUrl() , httpRequest.getRouteParams() ,httpRequest.getQueryParams() , httpRequest.getBodyCharset());

            URL url = new URL(completedUrl);
            //1.1如果需要则设置代理
            ProxyInfo proxyInfo = getProxyInfoWithDefault(httpRequest.getProxyInfo());
            connection = (null != proxyInfo) ?
                    (HttpURLConnection)url.openConnection(proxyInfo.getProxy()) :
                    (HttpURLConnection) url.openConnection();



            ////////////////////////////////////ssl处理///////////////////////////////////
            if(connection instanceof HttpsURLConnection){
                //默认设置这些
                HttpsURLConnection con = (HttpsURLConnection)connection;
                initSSL(con , getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier()) ,
                        getSSLSocketFactoryWithDefault(httpRequest.getSslSocketFactory()));
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            //2.处理header
            MultiValueMap<String, String> headers = mergeDefaultHeaders(httpRequest.getHeaders());

            ///HttpURLConnection不能用以下方法处理，会出现重复Cookie，即同样的Cookie框架自己弄了一份，我们手动又弄了一份
            /*headers = handleCookieIfNecessary(completedUrl, headers);*/

            //在需要开启Cookie功能的时候，只需要确保设置了CookieHandler的CookieHandler即可，HttpURLConnection会自动管理
            if(null != getCookieHandler()){
                if(null == CookieHandler.getDefault()){
                    CookieHandler.setDefault(getCookieHandler());
                }
            }

            setConnectProperty(connection, method, httpRequest.getContentType(), headers,
                    getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()),
                    getReadTimeoutWithDefault(httpRequest.getReadTimeout()));

            //3.留给子类复写的机会:给connection设置更多参数
            doWithConnection(connection);

            //4.写入内容，只对post有效
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(connection);
            }

            //5.连接
            connection.connect();

            //6.获取返回值
            int statusCode = connection.getResponseCode();

            inputStream = getStreamFrom(connection , statusCode , httpRequest.isIgnoreResponseBody());

            //7.返回header,包括Cookie处理
            boolean includeHeaders = httpRequest.isIncludeHeaders();
            if(null != getCookieHandler()){
                includeHeaders = top.jfunc.common.http.request.HttpRequest.INCLUDE_HEADERS;
            }
            MultiValueMap<String, String> parseHeaders = parseHeaders(connection, includeHeaders);

            ///存入Cookie
            /*if(null != getCookieHandler() && null != parseHeaders){
                CookieHandler cookieHandler = getCookieHandler();
                cookieHandler.put(URI.create(completedUrl) , parseHeaders);
            }*/

            return resultCallback.convert(statusCode , inputStream,
                    getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    parseHeaders);
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            //关闭顺序不能改变，否则服务端可能出现这个异常  严重: java.io.IOException: 远程主机强迫关闭了一个现有的连接
            //1 . 关闭连接
            disconnectQuietly(connection);
            //2 . 关闭流
            IoUtil.close(inputStream);
        }
    }



    @Override
    public Response get(HttpRequest req) throws IOException {
        HttpRequest request = beforeTemplate(req);
        Response response = template(request , Method.GET , null , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response post(StringBodyRequest req) throws IOException {
        StringBodyRequest request = beforeTemplate(req);
        String body = request.getBody();
        Response response = template(request, Method.POST ,
                connection -> writeContent(connection, body, getBodyCharsetWithDefault(request.getBodyCharset())),
                Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response httpMethod(HttpRequest req, Method method) throws IOException {
        HttpRequest httpRequest = beforeTemplate(req);
        ContentCallback<HttpURLConnection> contentCallback = null;
        if(method.hasContent() && httpRequest instanceof StringBodyRequest){
            String body = ((StringBodyRequest)httpRequest).getBody();
            contentCallback = connection -> writeContent(connection, body, getBodyCharsetWithDefault(httpRequest.getBodyCharset()));
        }
        Response response = template(httpRequest, method , contentCallback , Response::with);
        return afterTemplate(httpRequest , response);
    }

    @Override
    public byte[] getAsBytes(HttpRequest req) throws IOException {
        HttpRequest request = beforeTemplate(req);
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File download(DownLoadRequest req) throws IOException {
        DownLoadRequest request = beforeTemplate(req);
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(UploadRequest req) throws IOException {
        UploadRequest request = beforeTemplate(req);
        MultiValueMap<String, String> headers = mergeHeaders(request.getHeaders());
        Response response = template(request.setHeaders(headers), Method.POST ,
                connect -> this.upload0(connect, request.getFormParams(), request.getFormFiles()) , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response afterTemplate(HttpRequest request, Response response) throws IOException{
        if(request.isRedirectable() && response.needRedirect()){
            return get(GetRequest.of(response.getRedirectUrl()));
        }
        return response;
    }
}
