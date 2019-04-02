package top.jfunc.common.http.base;

import top.jfunc.common.http.Header;
import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.HttpStatus;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ssl.DefaultTrustManager;
import top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder;
import top.jfunc.common.http.base.ssl.TrustAnyHostnameVerifier;
import top.jfunc.common.http.smart.Request;
import top.jfunc.common.http.smart.SSLRequest;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.IoUtil;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Set;

/**
 * 使用URLConnection实现的Http公共父类
 * @author 熊诗言2018/6/6
 */
public abstract class AbstractNativeHttp extends AbstractHttp implements HttpTemplate<HttpURLConnection> {
    @Override
    public <R> R template(Request request, Method method, ContentCallback<HttpURLConnection> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        HttpURLConnection connect = null;
        InputStream inputStream = null;
        try {
            //1.获取连接
            String completedUrl = addBaseUrlIfNecessary(request.getUrl());

            connect = (HttpURLConnection)new java.net.URL(completedUrl).openConnection();

            //2.处理header
            setConnectProperty(connect, method, request.getContentType(), request.getHeaders(),
                    getConnectionTimeoutWithDefault(request.getConnectionTimeout()),
                    getReadTimeoutWithDefault(request.getReadTimeout()));


            ////////////////////////////////////ssl处理///////////////////////////////////
            if(connect instanceof HttpsURLConnection){
                //默认设置这些
                HttpsURLConnection con = (HttpsURLConnection)connect;
                initSSL(con , getHostnameVerifier(request) , getSSLSocketFactory(request));
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            //3.留给子类复写的机会:给connection设置更多参数
            doWithConnection(connect);

            //5.写入内容，只对post有效
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(connect);
            }

            //4.连接
            connect.connect();

            //6.获取返回值
            int statusCode = connect.getResponseCode();

            inputStream = getStreamFrom(connect , statusCode);

            return resultCallback.convert(statusCode , inputStream, getResultCharsetWithDefault(request.getResultCharset()), request.isIncludeHeaders() ? connect.getHeaderFields() : new HashMap<>(0));
            ///返回Response
            //return Response.with(statusCode , inputStream , request.getResultCharset() , request.isIncludeHeaders() ? connect.getHeaderFields() : new HashMap<>(0));
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            //关闭顺序不能改变，否则服务端可能出现这个异常  严重: java.io.IOException: 远程主机强迫关闭了一个现有的连接
            //1 . 关闭连接
            disconnectQuietly(connect);
            //2 . 关闭流
            IoUtil.close(inputStream);
        }
    }


    @Override
    public <R> R template(String url, Method method, String contentType, ContentCallback<HttpURLConnection> contentCallback, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset , boolean includeHeaders , ResultCallback<R> resultCallback) throws IOException {
        //默认的https校验
        // 后面会处理的，这里就不需要了 initDefaultSSL(sslVer);

        HttpURLConnection connect = null;
        InputStream inputStream = null;
        try {
            //1.获取连接
            String completedUrl = addBaseUrlIfNecessary(url);
            connect = (HttpURLConnection)new java.net.URL(completedUrl).openConnection();

            //2.处理header
            setConnectProperty(connect, method, contentType, headers,connectTimeout,readTimeout);

            ////////////////////////////////////ssl处理///////////////////////////////////
            if(connect instanceof HttpsURLConnection){
                //默认设置这些
                HttpsURLConnection con = (HttpsURLConnection)connect;
                initSSL(con , getHostnameVerifier() , getSSLSocketFactory());
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            //3.留给子类复写的机会:给connection设置更多参数
            doWithConnection(connect);

            //5.写入内容，只对post有效
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(connect);
            }

            //4.连接
            connect.connect();

            //6.获取返回值
            int statusCode = connect.getResponseCode();
            ///保留起非200抛异常的方式
//            if( HttpStatus.HTTP_OK == statusCode){
//                //6.1获取body
//                /*InputStream is = connect.getInputStream();
//                byte[] result = IoUtil.stream2Bytes(is);
//                is.close();
//
//                String s = new String(result, resultCharset);*/
//
//                //6.2获取header
//
//                inputStream = connect.getInputStream();
//
//                return resultCallback.convert(inputStream, resultCharset, includeHeaders ? connect.getHeaderFields() : new HashMap<>(0));
//
//            } else{
//                String err = errMessage(connect);
//                throw new HttpException(statusCode,err,connect.getHeaderFields());
//            }

            inputStream = getStreamFrom(connect , statusCode);

            return resultCallback.convert(statusCode , inputStream, resultCharset, includeHeaders ? connect.getHeaderFields() : new HashMap<>(0));
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            //关闭顺序不能改变，否则服务端可能出现这个异常  严重: java.io.IOException: 远程主机强迫关闭了一个现有的连接
            //1 . 关闭连接
            disconnectQuietly(connect);
            //2 . 关闭流
            IoUtil.close(inputStream);
        }
    }

    private InputStream getStreamFrom(HttpURLConnection connect , int statusCode) throws IOException{
        InputStream inputStream;
        if(HttpStatus.HTTP_OK == statusCode){
            inputStream = connect.getInputStream();
        }else {
            inputStream = connect.getErrorStream();
        }
        if(null == inputStream){
            inputStream = emptyInputStream();
        }
        return inputStream;
    }

    /**子类复写需要首先调用此方法，保证http的功能*/
    protected void doWithConnection(HttpURLConnection connect) throws IOException{
        //default do nothing, give children a chance to do more config
    }

    /**
     * @see SSLSocketFactoryBuilder#build()
     * @see SSLSocketFactoryBuilder#build(String, String)
     */
    protected void initSSL(HttpsURLConnection con , HostnameVerifier hostnameVerifier , SSLSocketFactory sslSocketFactory) {
        // 验证域
        if(null != hostnameVerifier){
            con.setHostnameVerifier(hostnameVerifier);
        }
        if(null != sslSocketFactory){
            con.setSSLSocketFactory(sslSocketFactory);
        }
    }

    protected void setConnectProperty(HttpURLConnection connect, Method method, String contentType, ArrayListMultimap<String,String> headers, int connectTimeout, int readTimeout) throws ProtocolException {
        connect.setRequestMethod(method.name());
        connect.setDoOutput(true);
        connect.setUseCaches(false);
        if(null != headers) {
            Set<String> keySet = headers.keySet();
            keySet.forEach((k)->headers.get(k).forEach((v)->connect.addRequestProperty(k,v)));
        }
        if(null != contentType){
            connect.setRequestProperty(Header.CONTENT_TYPE.toString(), contentType);
        }
        connect.setConnectTimeout(connectTimeout);
        connect.setReadTimeout(readTimeout);
    }
    protected void writeContent(HttpURLConnection connect, String data, String bodyCharset) throws IOException {
        if (null == data || null == bodyCharset) {
            return;
        }
        OutputStream outputStream = connect.getOutputStream();
        outputStream.write(data.getBytes(bodyCharset));
        outputStream.close();
        /* PrintWriter out = new PrintWriter(connect.getOutputStream());
        if (null != data) {
            out.print(data);
            out.flush();
        }
        out.close();*/
    }

    /**
     * 获取输出流中的错误信息，针对HttpURLConnection
     * @param connect HttpURLConnection
     * @return 错误信息
     * @see HttpURLConnection
     * @throws IOException IO异常
     */
    protected String errMessage(HttpURLConnection connect) throws IOException {
        //如果服务器返回的HTTP状态不是HTTP_OK，则表示发生了错误，此时可以通过如下方法了解错误原因。
        // 通过getErrorStream了解错误的详情
        InputStream is = connect.getErrorStream();
        if(null==is){return "";}
        InputStreamReader isr = new InputStreamReader(is, HttpConstants.DEFAULT_CHARSET);
        BufferedReader in = new BufferedReader(isr);
        String inputLine;
        StringWriter bw = new StringWriter();
        while ((inputLine = in.readLine()) != null)
        {
            bw.write(inputLine);
            bw.write("\n");
        }
        bw.close();
        in.close();

        //disconnectQuietly(connect);

        return bw.getBuffer().toString();
    }

    public static void disconnectQuietly(HttpURLConnection connect) {
        if(null != connect){
            try {
                connect.disconnect();
            } catch (Exception e) {}
        }
    }

    protected void initDefaultSSL(String sslVer) {
        try {
            TrustManager[] tmCerts = new TrustManager[1];
            tmCerts[0] = new DefaultTrustManager();
            SSLContext sslContext = SSLContext.getInstance(sslVer);
            sslContext.init(null, tmCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            HostnameVerifier hostnameVerifier = new TrustAnyHostnameVerifier();
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        } catch (Exception e) {
        }
    }
}
