package top.jfunc.common.http.component.jdk;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.BaseHttpRequestExecutor;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.component.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * HTTPURLConnection实现的处理
 * @author xiongshiyan
 * @see 2020.12.01
 * @since 1.2.12
 */
public class JdkHttpRequestExecutor extends BaseHttpRequestExecutor<HttpURLConnection, HttpURLConnection> implements HttpRequestExecutor<HttpURLConnection> {
    private RequesterFactory<HttpURLConnection> httpURLConnectionFactory;

    public JdkHttpRequestExecutor() {
        super(new DefaultJdkStreamExtractor(), new DefaultJdkHeaderExtractor(), new DefaultJdkHeaderHandler());
        this.httpURLConnectionFactory = new DefaultJdkConnectionFactory();
    }

    public JdkHttpRequestExecutor(StreamExtractor<HttpURLConnection> responseStreamExtractor,
                                  HeaderExtractor<HttpURLConnection> responseHeaderExtractor,
                                  RequesterFactory<HttpURLConnection> httpURLConnectionFactory,
                                  HeaderHandler<HttpURLConnection> httpURLConnectionHeaderHandler) {
        super(responseStreamExtractor, responseHeaderExtractor, httpURLConnectionHeaderHandler);
        this.httpURLConnectionFactory = httpURLConnectionFactory;
    }

    public JdkHttpRequestExecutor(ContentCallbackHandler<HttpURLConnection> contentCallbackHandler,
                                  StreamExtractor<HttpURLConnection> responseStreamExtractor,
                                  HeaderExtractor<HttpURLConnection> responseHeaderExtractor,
                                  HeaderHandler<HttpURLConnection> requestHeaderHandler,
                                  RequesterFactory<HttpURLConnection> httpURLConnectionFactory) {
        super(contentCallbackHandler, responseStreamExtractor, responseHeaderExtractor, requestHeaderHandler);
        this.httpURLConnectionFactory = httpURLConnectionFactory;
    }

    @Override
    public ClientHttpResponse execute(HttpRequest httpRequest, ContentCallback<HttpURLConnection> contentCallback) throws IOException{
        //1.初始化connection
        HttpURLConnection connection = getHttpURLConnectionFactory().create(httpRequest);

        //2.处理header[必须在写入body之前就设置好header]
        handleHeaders(connection, httpRequest);

        //3.写入内容
        handleBody(connection , contentCallback , httpRequest);

        //4.连接
        getResponse(connection, httpRequest);

        return new JdkClientHttpResponse(connection, httpRequest, getResponseStreamExtractor(), getResponseHeaderExtractor());
    }

    protected HttpURLConnection getResponse(HttpURLConnection connection, HttpRequest httpRequest) throws IOException {
        connection.connect();
        return connection;
    }

    public RequesterFactory<HttpURLConnection> getHttpURLConnectionFactory() {
        return httpURLConnectionFactory;
    }
}
