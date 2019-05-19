package top.jfunc.common.http.smart;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.base.handler.ToString;
import top.jfunc.common.http.base.handler.ToStringHandler;
import top.jfunc.common.http.base.ssl.DefaultTrustManager2;
import top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder;
import top.jfunc.common.http.base.ssl.TrustAnyHostnameVerifier;
import top.jfunc.common.http.kv.Header;
import top.jfunc.common.http.kv.Parameter;
import top.jfunc.common.http.request.DownLoadRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.StrUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.util.*;

/**
 * 代表一个Http请求的所有参数,基于Request-Response的可以更好地扩展功能
 * @see top.jfunc.common.http.basic.HttpClient
 * @see SmartHttpClient
 *
 * !!!此类作为以前的大杂烩，什么样的请求都放到一起，给设置参数的时候造成困扰，已经不适应快速发展的需要
 * 现将其一拆为多，针对不同的请求使用不同的请求即可
 *
 * @see top.jfunc.common.http.request.HttpRequest
 *
 * @see top.jfunc.common.http.request.impl.BaseRequest
 * @see top.jfunc.common.http.request.impl.PostBodyRequest
 * @see top.jfunc.common.http.request.impl.FormBodyRequest
 * @see top.jfunc.common.http.request.impl.FileParamUploadRequest
 * @see top.jfunc.common.http.request.impl.DownLoadRequest
 * @author xiongshiyan at 2017/12/9
 *
 * @since 从1.1开始建议不要用此类了,而是使用以上的一些意义更明确的
 */
public class Request implements HttpRequest , StringBodyRequest , UploadRequest , DownLoadRequest{
    /**
     * 结果包含headers
     */
    public static final boolean INCLUDE_HEADERS = true;
    /**
     * 结果忽略body
     */
    public static final boolean IGNORE_RESPONSE_BODY = true;
    /**
     * 支持重定向
     */
    public static final boolean REDIRECTABLE = true;


    /**
     * 请求的URL
     */
    private String url;
    /**
     * 路径参数，形如这种URL http://httpbin.org/book/{id}，保存id和id的值
     * @since 1.0.4
     */
    private Map<String , String> routeParams;
    /**
     * 查询参数，拼装在URL后面 ?
     * @since 1.0.4
     */
    private MultiValueMap<String,String> queryParams;
    /**
     * form参数
     * POST请求，会作为body存在 并且设置Content-Type为 application/xxx-form-url-encoded
     */
    private MultiValueMap<String,String> formParams;
    /**
     * 请求头
     */
    private MultiValueMap<String,String> headers;
    /**
     * 针对POST存在，params这种加进来的参数最终拼接之后保存到这里
     * @see Method#hasContent()
     */
    private String body;
    /**
     * 资源类型
     */
    private String contentType = null;
    /**
     * 连接超时时间，不设置就使用系统默认的
     * @see top.jfunc.common.http.base.Config#defaultConnectionTimeout
     */
    private Integer connectionTimeout = null;
    /**
     * 读数据超时时间，不设置就使用系统默认的
     * @see top.jfunc.common.http.base.Config#defaultReadTimeout
     */
    private Integer readTimeout = null;
    /**
     * 请求体编码，不设置就使用系统默认的
     * @see top.jfunc.common.http.base.Config#defaultBodyCharset
     */
    private String bodyCharset = HttpConstants.DEFAULT_CHARSET;
    /**
     * 返回体编码，不设置就使用系统默认的
     * @see top.jfunc.common.http.base.Config#defaultResultCharset
     */
    private String resultCharset = HttpConstants.DEFAULT_CHARSET;

    /**
     * 返回结果中是否包含headers,默认不包含
     */
    private boolean includeHeaders = !INCLUDE_HEADERS;

    /**
     * 返回结果中是否忽略body,  true那么就不去读取body，提高效率, 默认不忽略
     */
    private boolean ignoreResponseBody = !IGNORE_RESPONSE_BODY;

    /**
     * 是否支持重定向
     */
    private boolean redirectable = !REDIRECTABLE;

    /**
     * 2018-06-18为了文件上传增加的
     */
    private List<FormFile> formFiles = null;
    /**
     * 为文件下载确定信息
     */
    private File file = null;
    /**
     * 代理设置,如果有就设置
     * Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port));
     */
    private ProxyInfo proxyInfo = null;



    /**
     * HostnameVerifier
     * @see top.jfunc.common.http.base.Config#hostnameVerifier
     */
    private HostnameVerifier hostnameVerifier = new TrustAnyHostnameVerifier();
    /**
     * SSLContext
     * @see top.jfunc.common.http.base.Config#sslContext
     */
    private SSLContext sslContext = SSLSocketFactoryBuilder.create().getSSLContext();
    /**
     * SSLSocketFactory
     * @see top.jfunc.common.http.base.Config#sslSocketFactory
     */
    private SSLSocketFactory sslSocketFactory = null;
    /**
     * X509TrustManager
     * @see top.jfunc.common.http.base.Config#x509TrustManager
     */
    private X509TrustManager x509TrustManager = new DefaultTrustManager2();

    public Request(String url){this.url = url;}

    /**
     * 静态方法创建请求
     * @param url URL
     * @return Request
     */
    public static Request of(String url){
        return new Request(url);
    }

    /**************************变种的Setter*******************************/
    public Request setUrl(String url) {
        this.url = url;
        return this;
    }

    public Request setRouteParams(Map<String, String> routeParams) {
        this.routeParams = Objects.requireNonNull(routeParams);
        return this;
    }

    public Request addRouteParam(String key , String value){
        if(null == this.routeParams){
            this.routeParams = new HashMap<>(2);
        }
        this.routeParams.put(key, value);
        return this;
    }

    public Request setFormParams(MultiValueMap<String, String> formParams) {
        this.formParams = Objects.requireNonNull(formParams);
        return this;
    }
    public Request setFormParams(ArrayListMultimap<String, String> formParams) {
        Objects.requireNonNull(formParams);
        this.formParams = ArrayListMultiValueMap.fromMap(formParams);
        return this;
    }
    public Request setFormParams(Map<String, String> formParams) {
        Objects.requireNonNull(formParams);
        this.formParams = ArrayListMultiValueMap.fromMap(formParams);
        return this;
    }
    public Request addFormParam(String key, String value){
        initFormParams();
        this.formParams.add(key, value);
        return this;
    }
    public Request addFormParam(String key, String... values){
        initFormParams();
        for (String value : values) {
            this.formParams.add(key , value);
        }
        return this;
    }
    public Request addFormParam(String key, Iterable<String> values){
        initFormParams();
        for (String value : values) {
            this.formParams.add(key , value);
        }
        return this;
    }
    public Request addFormParam(Parameter... parameters){
        for (Parameter parameter : parameters) {
            addFormParam(parameter.getKey() , parameter.getValue());
        }
        return this;
    }
    public Request addFormParam(Map.Entry<String , Iterable<String>>... parameters){
        for (Map.Entry<String , Iterable<String>> parameter : parameters) {
            addFormParam(parameter.getKey() , parameter.getValue());
        }
        return this;
    }
    private void initFormParams(){
        if(null == this.formParams){
            this.formParams = new ArrayListMultiValueMap<>(2);
        }
    }
    public Request setQueryParams(MultiValueMap<String, String> queryParams) {
        this.queryParams = Objects.requireNonNull(queryParams);
        return this;
    }
    public Request setQueryParams(ArrayListMultimap<String, String> queryParams) {
        Objects.requireNonNull(queryParams);
        this.queryParams = ArrayListMultiValueMap.fromMap(queryParams);
        return this;
    }
    public Request setQueryParams(Map<String, String> queryParams) {
        Objects.requireNonNull(queryParams);
        this.queryParams = ArrayListMultiValueMap.fromMap(queryParams);
        return this;
    }
    public Request addQueryParam(String key, String value){
        initQueryParams();
        this.queryParams.add(key, value);
        return this;
    }
    public Request addQueryParam(String key, String... values){
        initQueryParams();
        for (String value : values) {
            this.queryParams.add(key , value);
        }
        return this;
    }
    public Request addQueryParam(String key, Iterable<String> values){
        initQueryParams();
        for (String value : values) {
            this.queryParams.add(key , value);
        }
        return this;
    }
    public Request addQueryParam(Parameter... parameters){
        for (Parameter parameter : parameters) {
            addQueryParam(parameter.getKey() , parameter.getValue());
        }
        return this;
    }
    public Request addQueryParam(Map.Entry<String , Iterable<String>>... parameters){
        for (Map.Entry<String , Iterable<String>> parameter : parameters) {
            addQueryParam(parameter.getKey() , parameter.getValue());
        }
        return this;
    }
    private void initQueryParams(){
        if(null == this.queryParams){
            this.queryParams = new ArrayListMultiValueMap<>(2);
        }
    }
    @Override
    public Request setHeaders(MultiValueMap<String, String> headers) {
        this.headers = Objects.requireNonNull(headers);
        return this;
    }
    public Request setHeaders(ArrayListMultimap<String, String> headers) {
        Objects.requireNonNull(headers);
        this.headers = ArrayListMultiValueMap.fromMap(headers);
        return this;
    }
    public Request setHeaders(Map<String, String> headers) {
        Objects.requireNonNull(headers);
        this.headers = ArrayListMultiValueMap.fromMap(headers);
        return this;
    }
    public Request addHeader(String key, String value){
        initHeaders();
        this.headers.add(key, value);
        return this;
    }
    public Request addHeader(String key, String... values){
        initHeaders();
        for (String value : values) {
            this.headers.add(key , value);
        }
        return this;
    }
    public Request addHeader(String key, Iterable<String> values){
        initHeaders();
        for (String value : values) {
            this.headers.add(key , value);
        }
        return this;
    }
    public Request addHeader(Header... headers){
        for (Header header : headers) {
            addHeader(header.getKey() , header.getValue());
        }
        return this;
    }
    private void initHeaders(){
        if(null == this.headers){
            this.headers = new ArrayListMultiValueMap<>(2);
        }
    }

    public Request addFormHeader(){
        return setContentType(HttpConstants.FORM_URLENCODED_WITH_DEFAULT_CHARSET);
    }
    public Request addJsonHeader(){
        return setContentType(HttpConstants.JSON_WITH_DEFAULT_CHARSET);
    }
    public Request addXmlHeader(){
        return setContentType(HttpConstants.TEXT_XML_WITH_DEFAULT_CHARSET);
    }



    /**
     * 设置body,最好是调用{@link this#setBody(String, String)}同时设置Content-Type
     */
    public Request setBody(String body) {
        this.body = body;
        return this;
    }
    public Request setBody(String body , String contentType) {
        this.body = body;
        this.contentType = contentType;
        return this;
    }

    /**
     * 直接传输一个Java对象可以使用该方法
     * @param o Java对象
     * @param handler 将Java对象转换为String的策略接口
     * @return this
     */
    public <T> Request setBody(T o , ToStringHandler<T> handler){
        ToStringHandler<T> stringHandler = Objects.requireNonNull(handler, "handler不能为空");
        this.body = stringHandler.toString(o);
        return this;
    }
    public Request setBodyT(Object o , ToString handler){
        ToString toString = Objects.requireNonNull(handler, "handler不能为空");
        this.body = toString.toString(o);
        return this;
    }

    public Request setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Request setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public Request setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }
    public Request setBodyCharset(String bodyCharset) {
        this.bodyCharset = bodyCharset;
        return this;
    }

    public Request setResultCharset(String resultCharset) {
        this.resultCharset = resultCharset;
        return this;
    }

    public Request setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
        return this;
    }

    public Request setIgnoreResponseBody(boolean ignoreResponseBody) {
        this.ignoreResponseBody = ignoreResponseBody;
        return this;
    }

    public Request setRedirectable(boolean redirectable) {
        this.redirectable = redirectable;
        //要支持重定向必须header
        if(redirectable){
            this.includeHeaders = true;
        }
        return this;
    }

    public Request addFormFile(FormFile... formFiles) {
        if(null != formFiles){
            initFormFiles();
            this.formFiles.addAll(Arrays.asList(formFiles));
        }
        return this;
    }

    public Request setFile(File file) {
        this.file = file;
        return this;
    }
    public Request setFile(String filePath) {
        this.file = new File(filePath);
        return this;
    }

    public Request setProxy(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
        return this;
    }

    public Request setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }
    public Request setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }
    public Request setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    public Request setX509TrustManager(X509TrustManager x509TrustManager) {
        this.x509TrustManager = x509TrustManager;
        return this;
    }


    /****************************Getter**************************/
    @Override
    public String getUrl() {
        return url;
    }
    @Override
    public Map<String, String> getRouteParams() {
        return routeParams;
    }
    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return queryParams;
    }
    @Override
    public MultiValueMap<String, String> getFormParams() {
        return formParams;
    }
    @Override
    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }


    /**
     * 如果没有显式设置body而是通过params添加的，此时一般认为是想发起form请求，最好设置Content-Type
     * @see this#setContentType(String)
     */
    @Override
    public String getBody() {
        //如果没有Body就将params的参数拼接
        if(StrUtil.isBlank(body)){
            //没有显式设置就设置默认的
            if(null == this.contentType){
                this.contentType = HttpConstants.FORM_URLENCODED + ";charset=" + bodyCharset;
            }
            return ParamUtil.contactMap(formParams, bodyCharset);
        }
        return body;
    }
    @Override
    public String getContentType() {
        return contentType;
    }
    @Override
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }
    @Override
    public Integer getReadTimeout() {
        return readTimeout;
    }
    @Override
    public String getBodyCharset() {
        return bodyCharset;
    }
    @Override
    public String getResultCharset() {
        return resultCharset;
    }
    @Override
    public boolean isIncludeHeaders() {
        return includeHeaders;
    }
    @Override
    public boolean isIgnoreResponseBody() {
        return ignoreResponseBody;
    }
    @Override
    public boolean isRedirectable() {
        return redirectable;
    }
    @Override
    public FormFile[] getFormFiles() {
        initFormFiles();
        return this.formFiles.toArray(new FormFile[this.formFiles.size()]);
    }
    @Override
    public File getFile() {
        return file;
    }
    @Override
    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    private void initFormFiles(){
        if(null == this.formFiles){
            this.formFiles = new ArrayList<>(2);
        }
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    /**
     * 因为一般地 SslSocketFactory 都是从sslContext产生出来的 ， 所以如果没显式设置就从sslContext产生
     */
    @Override
    public SSLSocketFactory getSslSocketFactory() {
        if(null == sslSocketFactory && null != sslContext){
            return sslContext.getSocketFactory();
        }
        return sslSocketFactory;
    }

    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }

    @Override
    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

}
