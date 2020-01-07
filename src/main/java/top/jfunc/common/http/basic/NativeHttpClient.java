package top.jfunc.common.http.basic;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.*;
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
public class NativeHttpClient extends AbstractImplementHttpClient<HttpURLConnection> {
    @Override
    public <R> R doInternalTemplate(String url, Method method, String contentType, ContentCallback<HttpURLConnection> contentCallback, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset , boolean includeHeaders , ResultCallback<R> resultCallback) throws Exception {
        //默认的https校验
        // 后面会处理的，这里就不需要了 initDefaultSSL(sslVer);

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //1.获取连接
            String completedUrl = handleUrlIfNecessary(url);
            connection = createAndConfigConnection(method , completedUrl , connectTimeout ,readTimeout);

            //2.处理header
            configHeaders(connection , contentType , completedUrl , headers);

            //3.留给子类复写的机会:给connection设置更多参数
            doWithConnection(connection);

            //4.写入内容，只对post有效
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(connection);
            }

            //5.连接
            connection.connect();

            //6.获取返回值
            inputStream = getStreamFrom(connection , false);

            //7.处理header
            MultiValueMap<String, String> responseHeaders = determineHeaders(connection, completedUrl, includeHeaders);

            return resultCallback.convert(connection.getResponseCode() , inputStream,
                    getConfig().getResultCharsetWithDefault(resultCharset),
                    responseHeaders);
        } finally {
            //关闭顺序不能改变，否则服务端可能出现这个异常  严重: java.io.IOException: 远程主机强迫关闭了一个现有的连接
            //1 . 关闭连接
            disconnectQuietly(connection);
            //2 . 关闭流
            IoUtil.close(inputStream);
        }
    }

    protected HttpURLConnection createAndConfigConnection(Method method , String completedUrl , int connectionTimeout , int readTimeout) throws Exception{
        Config config = getConfig();
        URL url = new URL(completedUrl);
        //1.1如果需要则设置代理
        ProxyInfo proxyInfo = config.getDefaultProxyInfo();
        HttpURLConnection connection = (null != proxyInfo) ?
                (HttpURLConnection)url.openConnection(proxyInfo.getProxy()) :
                (HttpURLConnection) url.openConnection();

        ////////////////////////////////////ssl处理///////////////////////////////////
        if(connection instanceof HttpsURLConnection){
            //默认设置这些
            initSSL((HttpsURLConnection)connection);
        }
        ////////////////////////////////////ssl处理///////////////////////////////////

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        connection.setRequestMethod(method.name());
        connection.setConnectTimeout(config.getConnectionTimeoutWithDefault(connectionTimeout));
        connection.setReadTimeout(config.getReadTimeoutWithDefault(readTimeout));

        return connection;
    }

    protected void initSSL(HttpsURLConnection connection){
        NativeUtil.initSSL(connection,
                getDefaultHostnameVerifier() ,
                getDefaultSSLSocketFactory());
    }

    @Override
    protected void setRequestHeaders(Object target, String contentType, MultiValueMap<String, String> handledHeaders) throws IOException {
        NativeUtil.setRequestHeaders((HttpURLConnection)target , contentType , handledHeaders);
    }

    /**子类复写增加更多设置*/
    protected void doWithConnection(HttpURLConnection connect) throws IOException{
        //default do nothing, give children a chance to do more config
    }


    protected InputStream getStreamFrom(HttpURLConnection connect , boolean ignoreResponseBody) throws IOException {
        return NativeUtil.getStreamFrom(connect, connect.getResponseCode(), ignoreResponseBody);
    }

    @Override
    protected MultiValueMap<String, String> parseResponseHeaders(Object source, boolean includeHeaders) {
        return NativeUtil.parseHeaders((HttpURLConnection)source , includeHeaders);
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
        return "HttpClient implemented by JDK's HttpURLConnection";
    }
}
