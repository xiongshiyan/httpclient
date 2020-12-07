package top.jfunc.common.http.component.jdk;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.response.ClientHttpResponse;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * HTTPURLConnection实现的处理
 * @author xiongshiyan
 * @since 2020.12.01
 * @since 1.2.12
 */
public class JdkHttpRequestExecutor extends BaseHttpRequestExecutor<HttpURLConnection, HttpURLConnection> implements HttpRequestExecutor<HttpURLConnection> {
    private RequesterFactory<HttpURLConnection> httpURLConnectionFactory;

    public JdkHttpRequestExecutor() {
        super(new DefaultJdkHeaderHandler(), new DefaultJdkStreamExtractor(), new DefaultJdkHeaderExtractor());
        this.httpURLConnectionFactory = new DefaultJdkConnectionFactory();
    }

    public JdkHttpRequestExecutor(HeaderHandler<HttpURLConnection> httpURLConnectionHeaderHandler,
                                  StreamExtractor<HttpURLConnection> responseStreamExtractor,
                                  HeaderExtractor<HttpURLConnection> responseHeaderExtractor,
                                  RequesterFactory<HttpURLConnection> httpURLConnectionFactory) {
        super(httpURLConnectionHeaderHandler, responseStreamExtractor, responseHeaderExtractor);
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
