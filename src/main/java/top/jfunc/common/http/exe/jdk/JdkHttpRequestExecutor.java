package top.jfunc.common.http.exe.jdk;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.jdk.*;
import top.jfunc.common.http.exe.ClientHttpResponse;
import top.jfunc.common.http.exe.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;

public class JdkHttpRequestExecutor implements HttpRequestExecutor<HttpURLConnection> {

    private RequesterFactory<HttpURLConnection> httpURLConnectionFactory;
    private HeaderHandler<HttpURLConnection> httpURLConnectionHeaderHandler;
    private RequestSender<HttpURLConnection , HttpURLConnection> connectionSender;
    private StreamExtractor<HttpURLConnection> httpURLConnectionStreamExtractor;
    private HeaderExtractor<HttpURLConnection> httpURLConnectionHeaderExtractor;
    private ContentCallbackHandler<HttpURLConnection> contentCallbackHandler;

    public JdkHttpRequestExecutor() {
        init();
    }

    protected void init(){
        setHttpURLConnectionFactory(new DefaultJdkConnectionFactory());
        setHttpURLConnectionHeaderHandler(new DefaultJdkHeaderHandler());
        setConnectionSender(new DefaultJdkConnectionSender());
        setHttpURLConnectionStreamExtractor(new DefaultJdkStreamExtractor());
        setHttpURLConnectionHeaderExtractor(new DefaultJdkHeaderExtractor());
        setContentCallbackHandler(new DefaultContentCallbackHandler<>());
    }

    @Override
    public ClientHttpResponse execute(HttpRequest httpRequest, ContentCallback<HttpURLConnection> contentCallback) throws IOException{
        //1.初始化connection
        HttpURLConnection connection = getHttpURLConnectionFactory().create(httpRequest);

        //2.处理header[必须在写入body之前就设置好header]
        handleHeaders(connection, httpRequest);

        //3.写入内容
        getContentCallbackHandler().handle(connection , contentCallback , httpRequest);

        //4.连接
        connect(connection, httpRequest);

        return new JdkClientHttpResponse(connection, httpRequest, getHttpURLConnectionStreamExtractor(), getHttpURLConnectionHeaderExtractor());
    }

    protected HttpURLConnection connect(HttpURLConnection connection, HttpRequest httpRequest) throws IOException {
        return getConnectionSender().send(connection , httpRequest);
    }

    protected void handleHeaders(HttpURLConnection connection, HttpRequest httpRequest) throws IOException {
        getHttpURLConnectionHeaderHandler().configHeaders(connection , httpRequest);
    }

    public RequesterFactory<HttpURLConnection> getHttpURLConnectionFactory() {
        return httpURLConnectionFactory;
    }

    public void setHttpURLConnectionFactory(RequesterFactory<HttpURLConnection> httpURLConnectionFactory) {
        this.httpURLConnectionFactory = httpURLConnectionFactory;
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
        this.httpURLConnectionStreamExtractor = httpURLConnectionStreamExtractor;
    }

    public HeaderExtractor<HttpURLConnection> getHttpURLConnectionHeaderExtractor() {
        return httpURLConnectionHeaderExtractor;
    }

    public void setHttpURLConnectionHeaderExtractor(HeaderExtractor<HttpURLConnection> httpURLConnectionHeaderExtractor) {
        this.httpURLConnectionHeaderExtractor = httpURLConnectionHeaderExtractor;
    }

    public ContentCallbackHandler<HttpURLConnection> getContentCallbackHandler() {
        return contentCallbackHandler;
    }

    public void setContentCallbackHandler(ContentCallbackHandler<HttpURLConnection> contentCallbackHandler) {
        this.contentCallbackHandler = contentCallbackHandler;
    }
}
