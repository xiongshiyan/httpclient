package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.NativeHttpClient;
import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeSmartHttpClient extends NativeHttpClient implements SmartHttpClient, SmartInterceptorHttpTemplate<HttpURLConnection> {

    @Override
    public <R> R doTemplate(HttpRequest httpRequest, Method method, ContentCallback<HttpURLConnection> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        onBeforeIfNecessary(httpRequest, method);

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //1.获取连接
            /// ParamHolder queryParamHolder = httpRequest.queryParamHolder();
            /// RouteParamHolder routeParamHolder = httpRequest.routeParamHolder();
            /// String completedUrl = handleUrlIfNecessary(httpRequest.getUrl() , routeParamHolder.getMap() , queryParamHolder.getParams() , queryParamHolder.getParamCharset());
            String completedUrl = handleUrlIfNecessary(httpRequest.getUrl());

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

            connection.setRequestMethod(method.name());
            connection.setConnectTimeout(getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()));
            connection.setReadTimeout(getReadTimeoutWithDefault(httpRequest.getReadTimeout()));

            //2.处理header
            MultiValueMap<String, String> headers = mergeDefaultHeaders(httpRequest.getHeaders());

            ///HttpURLConnection不能用以下方法处理，会出现重复Cookie，即同样的Cookie框架自己弄了一份，我们手动又弄了一份
            /*headerHolder = handleCookieIfNecessary(completedUrl, headerHolder);*/

            //在需要开启Cookie功能的时候，只需要确保设置了CookieHandler的CookieHandler即可，HttpURLConnection会自动管理
            ///这段代码放到设置CookieHandler的时候，不必每次都执行一下
            /*if(null != getCookieHandler()){
                if(null == CookieHandler.getDefault()){
                    CookieHandler.setDefault(getCookieHandler());
                }
            }*/

            setRequestHeaders(connection, httpRequest.getContentType(), headers,
                    httpRequest.getOverwriteHeaders());

            //3.留给子类复写的机会:给connection设置更多参数
            doWithConnection(connection , httpRequest);

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
            if(supportCookie()){
                includeHeaders = HttpRequest.INCLUDE_HEADERS;
            }
            MultiValueMap<String, String> parseHeaders = parseHeaders(connection, includeHeaders);

            ///存入Cookie
            /*if(null != getCookieHandler() && null != parseHeaders){
                CookieHandler cookieHandler = getCookieHandler();
                cookieHandler.put(URI.create(completedUrl) , parseHeaders);
            }*/

            R convert = resultCallback.convert(statusCode, inputStream,
                    getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    parseHeaders);

            onAfterReturnIfNecessary(httpRequest , convert);

            return convert;
        } catch (IOException e) {
            onErrorIfNecessary(httpRequest , e);
            throw e;
        } catch (Exception e){
            onErrorIfNecessary(httpRequest , e);
            throw new RuntimeException(e);
        } finally {
            onAfterIfNecessary(httpRequest);
            //关闭顺序不能改变，否则服务端可能出现这个异常  严重: java.io.IOException: 远程主机强迫关闭了一个现有的连接
            //1 . 关闭连接
            disconnectQuietly(connection);
            //2 . 关闭流
            IoUtil.close(inputStream);
        }
    }
    /**子类复写增加更多设置*/
    protected void doWithConnection(HttpURLConnection connect , HttpRequest httpRequest) throws IOException{
        //default do nothing, give children a chance to do more config
    }


    @Override
    public Response get(HttpRequest request) throws IOException {
        return template(request , Method.GET , null , Response::with);
    }

    @Override
    public Response post(StringBodyRequest request) throws IOException {
        final String body = request.getBody();
        final String bodyCharset = calculateBodyCharset(request.getBodyCharset() , request.getContentType());
        return template(request, Method.POST ,
                connection -> writeContent(connection, body, bodyCharset),
                Response::with);
    }

    @Override
    public <R> R http(HttpRequest request, Method method, ResultCallback<R> resultCallback) throws IOException {
        ContentCallback<HttpURLConnection> contentCallback = null;
        if(method.hasContent() && request instanceof StringBodyRequest){
            StringBodyRequest bodyRequest = (StringBodyRequest) request;
            final String body = bodyRequest.getBody();
            final String bodyCharset = calculateBodyCharset(bodyRequest.getBodyCharset() , bodyRequest.getContentType());
            contentCallback = connection -> writeContent(connection, body, bodyCharset);
        }
        return template(request, method , contentCallback , resultCallback);
    }

    @Override
    public byte[] getAsBytes(HttpRequest request) throws IOException {
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File download(DownloadRequest request) throws IOException {
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(UploadRequest request) throws IOException {
        ///MultiValueMap<String, String> headers = mergeHeaders(request.headerHolder().getHeaders());
        ///request.headerHolder().setHeaders(headers);
        return template(request, Method.POST ,
                connect -> upload0(connect, request.getFormParams(),
                            calculateBodyCharset(request.getParamCharset() , request.getContentType()),
                            request.getFormFiles()),
                Response::with);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by JDK's HttpURLConnection";
    }
}
