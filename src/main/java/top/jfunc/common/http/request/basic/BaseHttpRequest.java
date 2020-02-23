package top.jfunc.common.http.request.basic;

import top.jfunc.common.ChainCall;
import top.jfunc.common.http.request.AbstractHttpRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.CharsetUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiongshiyan at 2019/7/5 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class BaseHttpRequest<THIS extends BaseHttpRequest> extends AbstractHttpRequest<THIS> implements HttpRequest , ChainCall<THIS> {
    /**
     * 设置的URL
     */
    private String url;
    /**
     * 路径参数
     */
    private Map<String, String> routeParams;
    /**
     * Query参数
     */
    private MultiValueMap<String, String> queryParams;
    /**
     * Query参数字符编码
     */
    private String queryParamCharset = CharsetUtil.UTF_8;
    /**
     * header，可能多值
     */
    private MultiValueMap<String, String> headers;

    /**
     * HostnameVerifier
     */
    private HostnameVerifier hostnameVerifier = null;
    /**
     * SSLContext
     */
    private SSLContext sslContext = null;
    /**
     * X509TrustManager
     */
    private X509TrustManager x509TrustManager = null;
    /**
     * 属性设置
     */
    private Map<String, Object> attributes;

    public BaseHttpRequest(String url) {
        this.url = url;
    }

    public BaseHttpRequest(URL url) {
        this.url = url.toString();
    }

    public BaseHttpRequest() {
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public THIS setUrl(String url) {
        this.url = url;
        return myself();
    }

    @Override
    public THIS setUrl(URL url) {
        setUrl(url.toString());
        return myself();
    }

    @Override
    public Map<String, String> getRouteParams() {
        return routeParams;
    }

    @Override
    public THIS addRouteParam(String key, String value) {
        if (null == routeParams) {
            routeParams = new HashMap<>(2);
        }
        routeParams.put(key, value);
        return myself();
    }

    @Override
    public THIS setRouteParams(Map<String, String> routeParams) {
        this.routeParams = routeParams;
        return myself();
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return queryParams;
    }

    @Override
    public String getQueryParamCharset() {
        return queryParamCharset;
    }

    @Override
    public THIS setQueryParamCharset(String paramCharset) {
        this.queryParamCharset = paramCharset;
        return myself();
    }

    @Override
    public THIS addQueryParam(String key, String value, String... values) {
        if (null == queryParams) {
            queryParams = new ArrayListMultiValueMap<>(2);
        }
        queryParams.add(key, value, values);
        return myself();
    }

    @Override
    public THIS setQueryParams(MultiValueMap<String, String> params) {
        this.queryParams = params;
        return myself();
    }

    @Override
    public THIS setQueryParams(Map<String, String> params) {
        if (MapUtil.notEmpty(params)) {
            this.queryParams = ArrayListMultiValueMap.fromMap(params);
        }
        return myself();
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public THIS setHeader(String key, String value) {
        initHeaders();
        headers.set(key, value);
        return myself();
    }

    @Override
    public THIS addHeader(String key, String value, String... values) {
        initHeaders();
        headers.add(key, value, values);
        return myself();
    }

    @Override
    public THIS setHeaders(MultiValueMap<String, String> headers) {
        this.headers = headers;
        return myself();
    }

    @Override
    public THIS setHeaders(Map<String, String> headers) {
        if (MapUtil.notEmpty(headers)) {
            this.headers = ArrayListMultiValueMap.fromMap(headers);
        }
        return myself();
    }

    private void initHeaders() {
        if (null == headers) {
            headers = new ArrayListMultiValueMap<>(2);
        }
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    @Override
    public THIS setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return myself();
    }

    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }

    @Override
    public THIS setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return myself();
    }

    @Override
    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    @Override
    public THIS setX509TrustManager(X509TrustManager x509TrustManager) {
        this.x509TrustManager = x509TrustManager;
        return myself();
    }

    @Override
    public THIS addAttribute(String key, Object value) {
        if (null == attributes) {
            attributes = new HashMap<>(2);
        }
        attributes.put(key, value);
        return myself();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}