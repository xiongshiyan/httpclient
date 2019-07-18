package top.jfunc.common.http.basic;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static top.jfunc.common.http.util.NativeUtil.*;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeHttpClient extends AbstractHttpClient<HttpURLConnection> implements HttpTemplate<HttpURLConnection>, HttpClient {
    @Override
    public <R> R doInternalTemplate(String url, Method method, String contentType, ContentCallback<HttpURLConnection> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , boolean includeHeaders , ResultCallback<R> resultCallback) throws Exception {
        //默认的https校验
        // 后面会处理的，这里就不需要了 initDefaultSSL(sslVer);

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //1.获取连接
            String completedUrl = addBaseUrlIfNecessary(url);
            connection = (HttpURLConnection)new java.net.URL(completedUrl).openConnection();

            ////////////////////////////////////ssl处理///////////////////////////////////
            if(connection instanceof HttpsURLConnection){
                //默认设置这些
                HttpsURLConnection con = (HttpsURLConnection)connection;
                initSSL(con , getHostnameVerifier() , getSSLSocketFactory());
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod(method.name());
            connection.setConnectTimeout(getConnectionTimeoutWithDefault(connectTimeout));
            connection.setReadTimeout(getReadTimeoutWithDefault(readTimeout));

            //2.处理header
            setRequestHeaders(connection, contentType, mergeDefaultHeaders(headers) , null);

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

            inputStream = getStreamFrom(connection , statusCode , false);

            return resultCallback.convert(statusCode , inputStream, getResultCharsetWithDefault(resultCharset), parseHeaders(connection, includeHeaders));
        } finally {
            //关闭顺序不能改变，否则服务端可能出现这个异常  严重: java.io.IOException: 远程主机强迫关闭了一个现有的连接
            //1 . 关闭连接
            disconnectQuietly(connection);
            //2 . 关闭流
            IoUtil.close(inputStream);
        }
    }
    /**子类复写需要首先调用此方法，保证http的功能*/
    protected void doWithConnection(HttpURLConnection connect) throws IOException{
        //default do nothing, give children a chance to do more config
    }

    @Override
    protected ContentCallback<HttpURLConnection> bodyContentCallback(String body, String bodyCharset, String contentType) throws IOException {
        return connect -> writeContent(connect , body , bodyCharset);
    }

    @Override
    protected ContentCallback<HttpURLConnection> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, FormFile[] formFiles) throws IOException {
        return connect -> upload0(connect , params , paramCharset , formFiles);
    }

    @Override
    public String toString() {
        return "HttpClient implemented by JDK's HttpURLConnection";
    }
}
