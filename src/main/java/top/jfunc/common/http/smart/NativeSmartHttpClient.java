package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.NativeUtil;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static top.jfunc.common.http.util.NativeUtil.*;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeSmartHttpClient extends AbstractSmartHttpClient<HttpURLConnection> {

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, Method method, ContentCallback<HttpURLConnection> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //1.获取连接
            /// ParamHolder queryParamHolder = httpRequest.queryParamHolder();
            /// RouteParamHolder routeParamHolder = httpRequest.routeParamHolder();
            /// String completedUrl = handleUrlIfNecessary(httpRequest.getUrl() , routeParamHolder.getMap() , queryParamHolder.getParams() , queryParamHolder.getParamCharset());
            String completedUrl = handleUrlIfNecessary(httpRequest.getUrl());
            //初始化connection
            connection = createAndConfigConnection(httpRequest, method , completedUrl);

            //2.处理header
            configHeaders(connection , httpRequest , completedUrl);

            //3.留给子类复写的机会:给connection设置更多参数
            doWithConnection(connection , httpRequest);

            //4.写入内容，只对post有效
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(connection);
            }

            //5.连接
            connection.connect();

            //6.获取返回值
            inputStream = getStreamFrom(connection , httpRequest);

            //7.返回header,包括Cookie处理
            MultiValueMap<String, String> responseHeaders = determineHeaders(connection, httpRequest , completedUrl);

            return resultCallback.convert(connection.getResponseCode(), inputStream,
                    getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    responseHeaders);
        } finally {
            //关闭顺序不能改变，否则服务端可能出现这个异常  严重: java.io.IOException: 远程主机强迫关闭了一个现有的连接
            //1 . 关闭连接
            disconnectQuietly(connection);
            //2 . 关闭流
            IoUtil.close(inputStream);
        }
    }

    protected HttpURLConnection createAndConfigConnection(HttpRequest httpRequest , Method method , String completedUrl) throws Exception{
        URL url = new URL(completedUrl);
        //1.1如果需要则设置代理
        ProxyInfo proxyInfo = getProxyInfoWithDefault(httpRequest.getProxyInfo());
        HttpURLConnection connection = (null != proxyInfo) ?
                (HttpURLConnection)url.openConnection(proxyInfo.getProxy()) :
                (HttpURLConnection) url.openConnection();

        ////////////////////////////////////ssl处理///////////////////////////////////
        if(connection instanceof HttpsURLConnection){
            //默认设置这些
            initSSL((HttpsURLConnection)connection , httpRequest);
        }
        ////////////////////////////////////ssl处理///////////////////////////////////

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        connection.setRequestMethod(method.name());
        connection.setConnectTimeout(getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()));
        connection.setReadTimeout(getReadTimeoutWithDefault(httpRequest.getReadTimeout()));

        return connection;
    }

    protected void initSSL(HttpsURLConnection connection , HttpRequest httpRequest){
        NativeUtil.initSSL(connection, getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier()) ,
                getSSLSocketFactoryWithDefault(httpRequest.getSslSocketFactory()));
    }

    /**
     * {@link HttpURLConnection} 自己实现了cookie的管理
     * @param completedUrl URL
     * @param headers 正常用户的Header Map
     * @return 不改变原来的header
     * @throws IOException IOException
     */
    @Override
    protected MultiValueMap<String, String> addCookieIfNecessary(String completedUrl, MultiValueMap<String, String> headers) throws IOException {
        return headers;
    }

    @Override
    protected void setRequestHeaders(Object target, HttpRequest httpRequest, MultiValueMap<String, String> handledHeaders) throws IOException {
        NativeUtil.setRequestHeaders((HttpURLConnection)target , httpRequest.getContentType() , handledHeaders);
    }

    /**子类复写增加更多设置*/
    protected void doWithConnection(HttpURLConnection connect , HttpRequest httpRequest) throws IOException{
        //default do nothing, give children a chance to do more config
    }


    protected InputStream getStreamFrom(HttpURLConnection connect , HttpRequest httpRequest) throws IOException {
        return NativeUtil.getStreamFrom(connect, connect.getResponseCode(), httpRequest.isIgnoreResponseBody());
    }

    /**
     * {@link HttpURLConnection} 自己实现了cookie的管理
     * @param httpRequest HttpRequest
     * @param responseHeaders responseHeaders
     * @param completedUrl URL
     * @throws IOException IOException
     */
    @Override
    protected void saveCookieIfNecessary(HttpRequest httpRequest, MultiValueMap<String, String> responseHeaders, String completedUrl) throws IOException {
        //do nothing，HttpURLConnection自己会处理
    }

    @Override
    protected MultiValueMap<String, String> parseResponseHeaders(Object source, HttpRequest httpRequest) {
        return NativeUtil.parseHeaders((HttpURLConnection)source , httpRequest.isIncludeHeaders());
    }

    @Override
    protected ContentCallback<HttpURLConnection> bodyContentCallback(Method method , String body, String bodyCharset, String contentType) throws IOException {
        return connect -> writeContent(connect , body , bodyCharset);
    }

    @Override
    protected ContentCallback<HttpURLConnection> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        return connect -> upload0(connect , params , paramCharset , formFiles);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by JDK's HttpURLConnection";
    }
}
