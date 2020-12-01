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
    private RequestSender<HttpURLConnection , HttpURLConnection> connectionSender;

    public JdkHttpRequestExecutor() {
        super(new DefaultJdkStreamExtractor(), new DefaultJdkHeaderExtractor(), new DefaultJdkHeaderHandler());
        this.httpURLConnectionFactory = new DefaultJdkConnectionFactory();
        this.connectionSender = new DefaultJdkConnectionSender();
    }

    public JdkHttpRequestExecutor(StreamExtractor<HttpURLConnection> responseStreamExtractor,
                                  HeaderExtractor<HttpURLConnection> responseHeaderExtractor,
                                  RequesterFactory<HttpURLConnection> httpURLConnectionFactory,
                                  HeaderHandler<HttpURLConnection> httpURLConnectionHeaderHandler,
                                  RequestSender<HttpURLConnection, HttpURLConnection> connectionSender) {
        super(responseStreamExtractor, responseHeaderExtractor, httpURLConnectionHeaderHandler);
        this.httpURLConnectionFactory = httpURLConnectionFactory;
        this.connectionSender = connectionSender;
    }

    public JdkHttpRequestExecutor(ContentCallbackHandler<HttpURLConnection> contentCallbackHandler,
                                  StreamExtractor<HttpURLConnection> responseStreamExtractor,
                                  HeaderExtractor<HttpURLConnection> responseHeaderExtractor,
                                  HeaderHandler<HttpURLConnection> requestHeaderHandler,
                                  RequesterFactory<HttpURLConnection> httpURLConnectionFactory,
                                  RequestSender<HttpURLConnection, HttpURLConnection> connectionSender) {
        super(contentCallbackHandler,
                responseStreamExtractor,
                responseHeaderExtractor,
                requestHeaderHandler);
        this.httpURLConnectionFactory = httpURLConnectionFactory;
        this.connectionSender = connectionSender;
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

    public RequesterFactory<HttpURLConnection> getHttpURLConnectionFactory() {
        return httpURLConnectionFactory;
    }

    public RequestSender<HttpURLConnection, HttpURLConnection> getConnectionSender() {
        return connectionSender;
    }
}
