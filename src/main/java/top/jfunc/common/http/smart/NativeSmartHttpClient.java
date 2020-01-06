package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.basic.GetRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Objects;

import static top.jfunc.common.http.util.NativeUtil.*;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeSmartHttpClient extends AbstractImplementSmartHttpClient<HttpURLConnection> {

    private CompletedUrlCreator completedUrlCreator                                 = new DefaultCompletedUrlCreator();
    private RequesterFactory<HttpURLConnection> httpURLConnectionFactory            = new DefaultHttpURLConnectionFactory();
    private HeaderHandler<HttpURLConnection> httpURLConnectionHeaderHandler         = new DefaultHttpURLConnectionHeaderHandler();
    private RequestSender<HttpURLConnection , HttpURLConnection> connectionSender   = new DefaultHttpURLConnectionSender();
    private StreamExtractor<HttpURLConnection> httpURLConnectionStreamExtractor     = new DefaultHttpURLConnectionStreamExtractor();
    private HeaderExtractor<HttpURLConnection> httpURLConnectionHeaderExtractor     = new DefaultHttpURLConnectionHeaderExtractor();

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, Method method, ContentCallback<HttpURLConnection> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //1.获取连接
            String completedUrl = getCompletedUrlCreator().complete(httpRequest);

            //初始化connection
            connection = getHttpURLConnectionFactory().create(httpRequest, method , completedUrl);

            //2.处理header
            getHttpURLConnectionHeaderHandler().configHeaders(connection , httpRequest , completedUrl);

            //3.写入内容，只对post有效
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(connection);
            }

            //4.连接
            getConnectionSender().send(connection);

            //5.获取返回值
            inputStream = getHttpURLConnectionStreamExtractor().extract(connection , httpRequest , completedUrl);

            //6.返回header,包括Cookie处理
            MultiValueMap<String, String> responseHeaders = getHttpURLConnectionHeaderExtractor().extract(connection, httpRequest, completedUrl);

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

    @SuppressWarnings("unchecked")
    @Override
    public <R> R afterTemplate(HttpRequest httpRequest, R response) throws IOException {
        //1.设置支持重定向
        //2.返回值是Response
        if(httpRequest.followRedirects() && response instanceof Response){
            Response resp = (Response) response;
            if (resp.needRedirect()) {
                return (R)get(GetRequest.of(resp.getRedirectUrl()));
            }else {
                return response;
            }
        }

        return response;
    }

    @Override
    protected ContentCallback<HttpURLConnection> bodyContentCallback(Method method , String body, String bodyCharset, String contentType) throws IOException {
        return connect -> writeContent(connect , body , bodyCharset);
    }

    @Override
    protected ContentCallback<HttpURLConnection> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        return connect -> upload0(connect , params , paramCharset , formFiles);
    }






    public CompletedUrlCreator getCompletedUrlCreator() {
        return completedUrlCreator;
    }

    public void setCompletedUrlCreator(CompletedUrlCreator completedUrlCreator) {
        this.completedUrlCreator = Objects.requireNonNull(completedUrlCreator);
    }

    public RequesterFactory<HttpURLConnection> getHttpURLConnectionFactory() {
        return httpURLConnectionFactory;
    }

    public void setHttpURLConnectionFactory(RequesterFactory<HttpURLConnection> httpURLConnectionFactory) {
        this.httpURLConnectionFactory = Objects.requireNonNull(httpURLConnectionFactory);
    }

    public HeaderHandler<HttpURLConnection> getHttpURLConnectionHeaderHandler() {
        return httpURLConnectionHeaderHandler;
    }

    public void setHttpURLConnectionHeaderHandler(HeaderHandler<HttpURLConnection> httpURLConnectionHeaderHandler) {
        this.httpURLConnectionHeaderHandler = httpURLConnectionHeaderHandler;
    }

    public RequestSender<HttpURLConnection, HttpURLConnection> getConnectionSender() {
        return connectionSender;
    }

    public void setConnectionSender(RequestSender<HttpURLConnection, HttpURLConnection> connectionSender) {
        this.connectionSender = connectionSender;
    }

    public StreamExtractor<HttpURLConnection> getHttpURLConnectionStreamExtractor() {
        return httpURLConnectionStreamExtractor;
    }

    public void setHttpURLConnectionStreamExtractor(StreamExtractor<HttpURLConnection> httpURLConnectionStreamExtractor) {
        this.httpURLConnectionStreamExtractor = Objects.requireNonNull(httpURLConnectionStreamExtractor);
    }

    public HeaderExtractor<HttpURLConnection> getHttpURLConnectionHeaderExtractor() {
        return httpURLConnectionHeaderExtractor;
    }

    public void setHttpURLConnectionHeaderExtractor(HeaderExtractor<HttpURLConnection> httpURLConnectionHeaderExtractor) {
        this.httpURLConnectionHeaderExtractor = Objects.requireNonNull(httpURLConnectionHeaderExtractor);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by JDK's HttpURLConnection";
    }
}
