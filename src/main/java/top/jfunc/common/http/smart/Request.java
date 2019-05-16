package top.jfunc.common.http.smart;

import top.jfunc.common.http.Header;
import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.base.handler.ToString;
import top.jfunc.common.http.base.handler.ToStringHandler;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.StrUtil;

import java.io.File;
import java.util.*;

/**
 * 代表一个Http请求的所有参数,基于Request-Response的可以更好地扩展功能
 * @see top.jfunc.common.http.basic.HttpClient
 * @see SmartHttpClient
 * @see SSLRequest
 * @author xiongshiyan at 2017/12/9
 */
public class Request {
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
     */
    private Map<String , String> routeParams;
    /**
     * 请求参数
     * 1.GET请求，会拼接在url后面
     * 2.POST请求，会作为body存在 并且设置Content-Type为 application/xxx-form-url-encoded
     */
    private ArrayListMultimap<String,String> params;
    /**
     * 请求头
     */
    private ArrayListMultimap<String,String> headers;
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
    private List<FormFile> formFiles = new ArrayList<>();
    /**
     * 为文件下载确定信息
     */
    private File file = null;
    /**
     * 代理设置,如果有就设置
     * Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port));
     */
    private ProxyInfo proxyInfo = null;

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
        this.routeParams = routeParams;
        return this;
    }

    public Request addRouteParam(String key , String value){
        if(null == this.routeParams){
            this.routeParams = new HashMap<>(2);
        }
        this.routeParams.put(key, value);
        return this;
    }

    public Request setParams(ArrayListMultimap<String, String> params) {
        this.params = Objects.requireNonNull(params);
        return this;
    }
    public Request setParams(Map<String, String> params) {
        initParams();
        params.forEach((k,v)->this.params.put(k,v));
        return this;
    }
    public Request addParam(String key, String value){
        initParams();
        this.params.put(key, value);
        return this;
    }
    private void initParams(){
        if(null == this.params){
            this.params = new ArrayListMultimap<>();
        }
    }
    public Request setHeaders(ArrayListMultimap<String, String> headers) {
        this.headers = Objects.requireNonNull(headers);
        return this;
    }
    public Request setHeaders(Map<String, String> headers) {
        initHeaders();
        headers.forEach((k,v)->this.headers.put(k,v));
        return this;
    }
    public Request addHeader(String key, String value){
        initHeaders();
        this.headers.put(key, value);
        return this;
    }

    public Request addContentType(String contentType){
        initHeaders();
        this.headers.put(Header.CONTENT_TYPE.name() , contentType);
        return this;
    }
    public Request addFormHeader(){
        return addContentType(HttpConstants.FORM_URLENCODED_WITH_DEFAULT_CHARSET);
    }
    public Request addJsonHeader(){
        return addContentType(HttpConstants.JSON_WITH_DEFAULT_CHARSET);
    }
    public Request addXmlHeader(){
        return addContentType(HttpConstants.TEXT_XML_WITH_DEFAULT_CHARSET);
    }

    private void initHeaders(){
        if(null == this.headers){
            this.headers = new ArrayListMultimap<>();
        }
    }

    public Request setBody(String body) {
        this.body = body;
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
        this.includeHeaders = true;
        return this;
    }

    public Request addFormFile(FormFile... formFiles) {
        if(null != formFiles){
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

    /****************************Getter**************************/
    public String getUrl() {
        return url;
    }

    public Map<String, String> getRouteParams() {
        return routeParams;
    }

    public ArrayListMultimap<String, String> getParams() {
        return params;
    }

    public ArrayListMultimap<String, String> getHeaders() {
        return headers;
    }

    /**
     * 如果没有显式设置body而是通过params添加的，此时一般认为是想发起form请求，最好设置Content-Type
     * @see this#setContentType(String)
     */
    public String getBodyIfNullWithParams() {
        //如果没有Body就将params的参数拼接
        if(StrUtil.isBlank(body)){
            //没有显式设置就设置默认的
            if(null == this.contentType){
                this.contentType = HttpConstants.FORM_URLENCODED + ";charset=" + bodyCharset;
            }
            return ParamUtil.contactMap(params , bodyCharset);
        }
        return body;
    }

    public String getBody() {
        return body;
    }

    public String getContentType() {
        return contentType;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public String getBodyCharset() {
        return bodyCharset;
    }

    public String getResultCharset() {
        return resultCharset;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public boolean isIgnoreResponseBody() {
        return ignoreResponseBody;
    }

    public boolean isRedirectable() {
        return redirectable;
    }

    public FormFile[] getFormFiles() {
        return this.formFiles.toArray(new FormFile[this.formFiles.size()]);
    }

    public File getFile() {
        return file;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }
}
