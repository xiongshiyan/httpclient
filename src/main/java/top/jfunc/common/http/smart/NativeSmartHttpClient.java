package top.jfunc.common.http.smart;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.jdk.*;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.basic.GetRequest;
import top.jfunc.common.http.util.NativeUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Objects;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeSmartHttpClient extends AbstractImplementSmartHttpClient<HttpURLConnection> {

    private RequesterFactory<HttpURLConnection> httpURLConnectionFactory;
    private HeaderHandler<HttpURLConnection> httpURLConnectionHeaderHandler;
    private RequestSender<HttpURLConnection , HttpURLConnection> connectionSender;
    private StreamExtractor<HttpURLConnection> httpURLConnectionStreamExtractor;
    private HeaderExtractor<HttpURLConnection> httpURLConnectionHeaderExtractor;

    private Closer connectionCloser;
    private Closer inputStreamCloser;

    public NativeSmartHttpClient(){
        setBodyContentCallbackCreator(new DefaultJdkBodyContentCallbackCreator());
        setUploadContentCallbackCreator(new DefaultJdkUploadContentCallbackCreator());

        setHttpURLConnectionFactory(new DefaultJdkConnectionFactory());
        setHttpURLConnectionHeaderHandler(new DefaultJdkHeaderHandler());
        setConnectionSender(new DefaultJdkConnectionSender());
        setHttpURLConnectionStreamExtractor(new DefaultJdkStreamExtractor());
        setHttpURLConnectionHeaderExtractor(new DefaultJdkHeaderExtractor());


        setConnectionCloser(new DefaultCloser());
        setInputStreamCloser(new DefaultCloser());

    }

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, ContentCallback<HttpURLConnection> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //1.初始化connection
            connection = getHttpURLConnectionFactory().create(httpRequest);

            //2.处理header[必须在写入body之前就设置好header]
            getHttpURLConnectionHeaderHandler().configHeaders(connection , httpRequest);

            //3.写入内容
            getContentCallbackHandler().handle(connection , contentCallback , httpRequest);

            //4.连接
            connect(connection, httpRequest);

            //5.获取返回值
            inputStream = getHttpURLConnectionStreamExtractor().extract(connection , httpRequest);

            //6.返回header,包括Cookie处理
            MultiValueMap<String, String> responseHeaders = getHttpURLConnectionHeaderExtractor().extract(connection, httpRequest);

            return resultCallback.convert(connection.getResponseCode(), inputStream,
                    getConfig().getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    responseHeaders);
        } finally {
            //关闭顺序不能改变，否则服务端可能出现这个异常  严重: java.io.IOException: 远程主机强迫关闭了一个现有的连接
            //1 . 关闭连接
            closeConnection(connection);
            //2 . 关闭流
            closeInputStream(inputStream);
        }
    }

    protected HttpURLConnection connect(HttpURLConnection connection, HttpRequest httpRequest) throws IOException {
        return getConnectionSender().send(connection , httpRequest);
    }

    protected void closeInputStream(InputStream inputStream) throws IOException {
        getInputStreamCloser().close(inputStream);
    }

    protected void closeConnection(HttpURLConnection connection) throws IOException {
        getConnectionCloser().close(new AbstractCloseAdapter<HttpURLConnection>(connection) {
            @Override
            protected void doClose(HttpURLConnection connection) throws IOException {
                NativeUtil.disconnectQuietly(connection);
            }
        });
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
        this.httpURLConnectionHeaderHandler = Objects.requireNonNull(httpURLConnectionHeaderHandler);
    }

    public RequestSender<HttpURLConnection, HttpURLConnection> getConnectionSender() {
        return connectionSender;
    }

    public void setConnectionSender(RequestSender<HttpURLConnection, HttpURLConnection> connectionSender) {
        this.connectionSender = Objects.requireNonNull(connectionSender);
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

    public Closer getConnectionCloser() {
        return connectionCloser;
    }

    public void setConnectionCloser(Closer connectionCloser) {
        this.connectionCloser = Objects.requireNonNull(connectionCloser);
    }

    public Closer getInputStreamCloser() {
        return inputStreamCloser;
    }

    public void setInputStreamCloser(Closer inputStreamCloser) {
        this.inputStreamCloser = Objects.requireNonNull(inputStreamCloser);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by JDK's HttpURLConnection";
    }
}
