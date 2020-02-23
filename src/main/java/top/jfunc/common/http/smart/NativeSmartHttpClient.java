package top.jfunc.common.http.smart;

import top.jfunc.common.http.base.*;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.jdk.*;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.NativeUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.ObjectUtil;

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

    public NativeSmartHttpClient(){
        setBodyContentCallbackCreator(new DefaultJdkBodyContentCallbackCreator());
        setUploadContentCallbackCreator(new DefaultJdkUploadContentCallbackCreator());

        setHttpURLConnectionFactory(new DefaultJdkConnectionFactory());
        setHttpURLConnectionHeaderHandler(new DefaultJdkHeaderHandler());
        setConnectionSender(new DefaultJdkConnectionSender());
        setHttpURLConnectionStreamExtractor(new DefaultJdkStreamExtractor());
        setHttpURLConnectionHeaderExtractor(new DefaultJdkHeaderExtractor());


        setConnectionCloser(new DefaultCloser());

        setCookieAccessor(new JdkCookieAccessor());
    }

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, ContentCallback<HttpURLConnection> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //1.初始化connection
            connection = getHttpURLConnectionFactory().create(httpRequest);

            //2.处理header[必须在写入body之前就设置好header]
            handleHeaders(connection, httpRequest);

            //3.写入内容
            getContentCallbackHandler().handle(connection , contentCallback , httpRequest);

            //4.连接
            connect(connection, httpRequest);

            //5.获取返回值
            inputStream = getHttpURLConnectionStreamExtractor().extract(connection , httpRequest);

            //6.返回header,包括Cookie处理
            MultiValueMap<String, String> responseHeaders = getHttpURLConnectionHeaderExtractor().extract(connection, httpRequest);

            //7.处理Cookie
            getCookieAccessor().saveCookieIfNecessary(httpRequest , responseHeaders);


            int statusCode = connection.getResponseCode();
            if(needRedirect(httpRequest, statusCode, responseHeaders)){
                String redirectUrl = responseHeaders.getFirst(HttpHeaders.LOCATION);
                HttpRequest hr = createRedirectHttpRequest(httpRequest, redirectUrl);
                return doInternalTemplate(hr, null, resultCallback);
            }


            return resultCallback.convert(statusCode, inputStream,
                    calculateResultCharset(httpRequest), responseHeaders);

        } finally {
            //关闭顺序不能改变，否则服务端可能出现这个异常  严重: java.io.IOException: 远程主机强迫关闭了一个现有的连接
            //1 . 关闭连接
            closeConnection(connection);
            //2 . 关闭流
            closeInputStream(inputStream);
        }
    }

    protected HttpRequest createRedirectHttpRequest(HttpRequest httpRequest, String redirectUrl) {
        HttpRequest hr = getHttpRequestFactory().create(redirectUrl , null , null , httpRequest.getConnectionTimeout() , httpRequest.getReadTimeout() , httpRequest.getResultCharset());
        init(hr , Method.GET);
        //处理多次重定向的情况
        hr.followRedirects(Config.FOLLOW_REDIRECTS);
        return hr;
    }

    /**
     * 判断是否重定向
     */
    protected boolean needRedirect(HttpRequest httpRequest, int statusCode, MultiValueMap<String, String> responseHeaders) {
        Config config = httpRequest.getConfig();
        boolean followRedirects = ObjectUtil.defaultIfNull(httpRequest.followRedirects() , config.followRedirects());
        return followRedirects && HttpStatus.needRedirect(statusCode)
                && MapUtil.notEmpty(responseHeaders)
                && responseHeaders.containsKey(HttpHeaders.LOCATION);
    }

    protected void handleHeaders(HttpURLConnection connection, HttpRequest httpRequest) throws IOException {
        getCookieAccessor().addCookieIfNecessary(httpRequest);
        getHttpURLConnectionHeaderHandler().configHeaders(connection , httpRequest);
    }

    protected HttpURLConnection connect(HttpURLConnection connection, HttpRequest httpRequest) throws IOException {
        return getConnectionSender().send(connection , httpRequest);
    }

    protected void closeInputStream(InputStream inputStream) throws IOException {
        getInputStreamCloser().close(inputStream);
    }

    protected void closeConnection(HttpURLConnection connection) throws IOException {
        getConnectionCloser().close(new HttpURLConnectionCloser(connection));
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

    @Override
    public String toString() {
        return "SmartHttpClient implemented by JDK's HttpURLConnection";
    }



    protected static class HttpURLConnectionCloser extends AbstractCloseAdapter<HttpURLConnection> {
        protected HttpURLConnectionCloser(HttpURLConnection connection){
            super(connection);
        }
        @Override
        protected void doClose(HttpURLConnection connection) throws IOException {
            NativeUtil.disconnectQuietly(connection);
        }
    }
}
