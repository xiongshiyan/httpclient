package top.jfunc.common.http.component.jdk;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.BaseHttpRequestExecutor;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.component.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;

public class JdkHttpRequestExecutor extends BaseHttpRequestExecutor<HttpURLConnection, HttpURLConnection> implements HttpRequestExecutor<HttpURLConnection> {

    private RequesterFactory<HttpURLConnection> httpURLConnectionFactory;
    private HeaderHandler<HttpURLConnection> httpURLConnectionHeaderHandler;
    private RequestSender<HttpURLConnection , HttpURLConnection> connectionSender;

    public JdkHttpRequestExecutor() {
        super(new DefaultJdkStreamExtractor(), new DefaultJdkHeaderExtractor());
        setHttpURLConnectionFactory(new DefaultJdkConnectionFactory());
        setHttpURLConnectionHeaderHandler(new DefaultJdkHeaderHandler());
        setConnectionSender(new DefaultJdkConnectionSender());
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

        return new JdkClientHttpResponse(connection, httpRequest, getResponseStreamExtractor(), getResponseHeaderExtractor());
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
}
