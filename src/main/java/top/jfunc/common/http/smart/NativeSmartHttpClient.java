package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.NativeHttpClient;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.IoUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeSmartHttpClient extends NativeHttpClient implements SmartHttpClient  , SmartHttpTemplate<HttpURLConnection>{

    @Override
    public NativeSmartHttpClient setConfig(Config config) {
        super.setConfig(config);
        return this;
    }

    @Override
    public <R> R template(Request request, Method method, ContentCallback<HttpURLConnection> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //1.获取连接
            String completedUrl = addBaseUrlIfNecessary(request.getUrl());

            URL url = new URL(completedUrl);
            //1.1如果需要则设置代理
            connection = (null != request.getProxyInfo()) ?
                    (HttpURLConnection)url.openConnection(request.getProxyInfo().getProxy()) :
                    (HttpURLConnection) url.openConnection();

            //2.处理header
            setConnectProperty(connection, method, request.getContentType(), mergeDefaultHeaders(request.getHeaders()),
                    getConnectionTimeoutWithDefault(request.getConnectionTimeout()),
                    getReadTimeoutWithDefault(request.getReadTimeout()));


            ////////////////////////////////////ssl处理///////////////////////////////////
            if(connection instanceof HttpsURLConnection){
                //默认设置这些
                HttpsURLConnection con = (HttpsURLConnection)connection;
                initSSL(con , RequestSSLUtil.getHostnameVerifier(request , getHostnameVerifier()) ,
                        RequestSSLUtil.getSSLSocketFactory(request , getSSLSocketFactory()));
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            //3.留给子类复写的机会:给connection设置更多参数
            doWithConnection(connection);

            //5.写入内容，只对post有效
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(connection);
            }

            //4.连接
            connection.connect();

            //6.获取返回值
            int statusCode = connection.getResponseCode();

            inputStream = getStreamFrom(connection , statusCode , request.isIgnoreResponseBody());

            return resultCallback.convert(statusCode , inputStream, getResultCharsetWithDefault(request.getResultCharset()), request.isIncludeHeaders() ? connection.getHeaderFields() : new HashMap<>(0));
            ///返回Response
            //return Response.with(statusCode , inputStream , request.getResultCharset() , request.isIncludeHeaders() ? connection.getHeaderFields() : new HashMap<>(0));
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
    public Response get(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(ParamUtil.contactUrlParams(request.getUrl(), request.getParams() , request.getBodyCharset()), Method.GET,
                request.getContentType(), null, request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(),
                request.getResultCharset(), request.isIncludeHeaders(), Response::with);*/
        Response response = template(request.setUrl(ParamUtil.contactUrlParams(request.getUrl(), request.getParams(), getBodyCharsetWithDefault(request.getBodyCharset()))) ,
                Method.GET , null , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response post(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(),
                connection -> writeContent(connection, request.getBody(), request.getBodyCharset()),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(),
                request.getResultCharset(), request.isIncludeHeaders(), Response::with);*/
        Response response = template(request, Method.POST , connection -> writeContent(connection, request.getBody(), getBodyCharsetWithDefault(request.getBodyCharset())) , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response httpMethod(Request req, Method method) throws IOException {
        Request request = beforeTemplate(req);
        ContentCallback<HttpURLConnection> contentCallback = null;
        if(method.hasContent()){
            contentCallback = connection -> writeContent(connection, request.getBody(), getBodyCharsetWithDefault(request.getBodyCharset()));
        }
        Response response = template(request, method , contentCallback , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public byte[] getAsBytes(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(), Method.GET, request.getContentType(), null,request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders() ,
                (s,b,r,h)-> IoUtil.stream2Bytes(b));*/
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(), Method.GET, request.getContentType(), null,request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders() ,
                (s,b,r,h)-> IoUtil.copy2File(b, request.getFile()));*/
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*ArrayListMultimap<String, String> headers = mergeHeaders(request.getHeaders(), request.getFormFiles());
        Response response = template(request.getUrl(), Method.POST, null, connect -> this.upload0(connect, request.getFormFiles()),
                headers, request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        /*使用更全的 ，支持文件和参数一起上传 */

        ArrayListMultimap<String, String> headers = mergeHeaders(request.getHeaders());
        /*Response response = template(request.getUrl(), Method.POST, null, connect -> this.upload0(connect, request.getParams() ,request.getFormFiles()),
                headers, request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request.setHeaders(headers), Method.POST ,
                connect -> this.upload0(connect, request.getParams(), request.getFormFiles()) , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response afterTemplate(Request request, Response response) throws IOException{
        if(request.isRedirectable() && response.needRedirect()){
            return get(Request.of(response.getRedirectUrl()));
        }
        return response;
    }
}
