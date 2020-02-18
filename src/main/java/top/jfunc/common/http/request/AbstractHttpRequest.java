package top.jfunc.common.http.request;

import top.jfunc.common.ChainCall;
import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.MediaType;
import top.jfunc.common.http.base.Method;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.component.CompletedUrlCreator;
import top.jfunc.common.http.component.DefaultCompletedUrlCreator;

/**
 * @author xiongshiyan at 2019/7/5 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractHttpRequest<THIS extends AbstractHttpRequest> implements HttpRequest , ChainCall<THIS>{
    private static final CompletedUrlCreator DEFAULT_COMPLETED_URL_CREATOR = new DefaultCompletedUrlCreator();

    /**
     * 最终的访问URL
     */
    private String cacheCompletedUrl;
    /**
     * 完整URL处理器
     */
    private CompletedUrlCreator completedUrlCreator = DEFAULT_COMPLETED_URL_CREATOR;
    /**
     * 资源类型
     */
    private String contentType = null;
    /**
     * 连接超时时间，不设置就使用系统默认的
     * @see Config#defaultConnectionTimeout
     */
    private int connectionTimeout = HttpConstants.TIMEOUT_UNSIGNED;
    /**
     * 读数据超时时间，不设置就使用系统默认的
     * @see Config#defaultReadTimeout
     */
    private int readTimeout = HttpConstants.TIMEOUT_UNSIGNED;
    /**
     * 返回体编码，不设置就使用系统默认的
     * @see Config#defaultResultCharset
     */
    private String resultCharset = null;
    /**
     * 返回结果中是否包含headers,默认不包含
     */
    private boolean retainResponseHeaders = !RETAIN_RESPONSE_HEADERS;
    /**
     * 返回结果中是否忽略body,  true那么就不去读取body，提高效率, 默认不忽略
     */
    private boolean ignoreResponseBody = !IGNORE_RESPONSE_BODY;
    /**
     * 是否支持重定向
     */
    private boolean followRedirects = !FOLLOW_REDIRECTS;
    /**
     * 代理设置,如果有就设置
     * Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port));
     */
    private ProxyInfo proxyInfo = null;

    /** 用于接收系统的默认设置 */
    private Config config;

    /** method */
    private Method method;

    @Override
    public String getCompletedUrl() {
        if(null == this.cacheCompletedUrl){
            this.cacheCompletedUrl = getCompletedUrlCreator().complete(this);
        }
        return this.cacheCompletedUrl;
    }

    public CompletedUrlCreator getCompletedUrlCreator() {
        return completedUrlCreator;
    }

    public void setCompletedUrlCreator(CompletedUrlCreator completedUrlCreator) {
        this.completedUrlCreator = completedUrlCreator;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public THIS setContentType(String contentType) {
        this.contentType = contentType;
        return myself();
    }

    @Override
    public THIS setContentType(MediaType mediaType) {
        this.contentType = mediaType.toString();
        return myself();
    }
    public THIS addFormHeader(){
        return setContentType(MediaType.APPLICATIPON_FORM_DATA.withCharset(HttpConstants.DEFAULT_CHARSET));
    }
    public THIS addJsonHeader(){
        return setContentType(MediaType.APPLICATIPON_JSON.withCharset(HttpConstants.DEFAULT_CHARSET));
    }
    public THIS addXmlHeader(){
        return setContentType(MediaType.TXT_XML.withCharset(HttpConstants.DEFAULT_CHARSET));
    }

    @Override
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public THIS setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return myself();
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public THIS setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return myself();
    }

    @Override
    public String getResultCharset() {
        return resultCharset;
    }

    @Override
    public THIS setResultCharset(String resultCharset) {
        this.resultCharset = resultCharset;
        return myself();
    }

    @Override
    public boolean retainResponseHeaders() {
        return retainResponseHeaders;
    }

    @Override
    public THIS retainResponseHeaders(boolean retainResponseHeaders) {
        this.retainResponseHeaders = retainResponseHeaders;
        return myself();
    }

    @Override
    public boolean ignoreResponseBody() {
        return ignoreResponseBody;
    }

    @Override
    public THIS ignoreResponseBody(boolean ignoreResponseBody) {
        this.ignoreResponseBody = ignoreResponseBody;
        return myself();
    }

    @Override
    public boolean followRedirects() {
        return followRedirects;
    }

    @Override
    public THIS followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return myself();
    }

    @Override
    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    @Override
    public THIS setProxy(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
        return myself();
    }

    @Override
    public Config getConfig() {
        return config;
    }

    /**
     * HttpRequest中的config都来自于实现类的初始化，调用此方法将系统的设置传递给HttpRequest
     * @param config config
     */
    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public THIS setMethod(Method method) {
        this.method = method;
        return myself();
    }


    /**
     * 默认只支持浅拷贝，请勿要对应用类型的field进行add、remove等操作，会互相影响，比如map.add、collection.remove
     */
    @SuppressWarnings("unchecked")
    @Override
    public THIS clone() throws CloneNotSupportedException {
        return (THIS)super.clone();
    }
}
