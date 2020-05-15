package top.jfunc.common.http.holderrequest;

import top.jfunc.common.http.holder.*;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.util.Map;

/**
 * Http请求的基本定义
 * @since 1.1
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface HolderHttpRequest extends HttpRequest {
    /**
     * 处理Url参数的
     * @return urlHolder must not be null
     */
    UrlHolder urlHolder();

    /**
     * 设置url处理器,其实holder相关的设置还可以通过 httpRequest.urlHolder()=..Holder来设置
     * @see UrlHolder
     * @see PhpUrlHolder
     * @param urlHolder urlHolder
     * @return this
     */
    HolderHttpRequest urlHolder(UrlHolder urlHolder);

    /**
     * 请求的URL
     * @return 请求的URL
     */
    @Override
    default String getUrl(){
        return urlHolder().getUrl();
    }

    /**
     * 设置URL
     * @param url url
     * @return this
     */
    @Override
    HolderHttpRequest setUrl(String url);

    /**
     *获取到 {@link RouteParamHolder} 可以对路径参数完全接管处理
     * @return RouteParamHolder must not be null
     */
    default RouteParamHolder routeParamHolder(){
        return urlHolder().routeParamHolder();
    }

    /**
     * 获取设置的路径参数
     * @return 路径参数
     */
    @Override
    default Map<String, String> getRouteParams(){
        return routeParamHolder().getMap();
    }

    /**
     * 便捷地设置路径参数
     * @param key key
     * @param value value
     * @return this
     */
    @Override
    default HolderHttpRequest addRouteParam(String key, String value){
        routeParamHolder().put(key, value);
        return this;
    }

    /**
     * 便捷地设置路径参数
     * @param routeParams 多个路径参数
     * @return this
     */
    @Override
    default HolderHttpRequest setRouteParams(Map<String, String> routeParams){
        routeParamHolder().setMap(routeParams);
        return this;
    }

    /**
     * 获取到 {@link ParamHolder} 可以对Query参数完全接管处理
     * @return ParamHolder must not be null
     */
    default ParamHolder queryParamHolder(){
        return urlHolder().queryParamHolder();
    }

    /**
     * 获取设置的Query参数
     * @return Query参数
     */
    @Override
    default MultiValueMap<String, String> getQueryParams(){
        return queryParamHolder().get();
    }

    /**
     * 提供便捷的设置Query参数的方法
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    @Override
    default HolderHttpRequest addQueryParam(String key, String value, String... values){
        queryParamHolder().add(key, value, values);
        return this;
    }

    /**
     * 提供便捷的设置Query参数的方法
     * @param queryParams 多个查询参数
     * @return this
     */
    @Override
    default HolderHttpRequest setQueryParams(MultiValueMap<String, String> queryParams){
        queryParamHolder().set(queryParams);
        return this;
    }

    /**
     * 提供便捷的设置Query参数的方法
     * @param queryParams 多个查询参数
     * @return this
     */
    @Override
    default HolderHttpRequest setQueryParams(Map<String, String> queryParams){
        queryParamHolder().set(queryParams);
        return this;
    }

    /**
     * 获取Query编码
     * @return Query编码
     */
    @Override
    default String getQueryParamCharset() {
        return queryParamHolder().getParamCharset();
    }

    /**
     * 设置Query参数编码
     * @param paramCharset 参数编码
     * @return this
     */
    @Override
    default HolderHttpRequest setQueryParamCharset(String paramCharset) {
        queryParamHolder().setParamCharset(paramCharset);
        return this;
    }

    /**
     * 获取到 {@link HeaderHolder} 可以对Header完全接管处理
     * add 方式处理
     * @return HeaderHolder must not be null
     */
    HeaderHolder headerHolder();

    /**
     * 便捷设置header，set方式
     * @param key key
     * @param value value
     * @return this
     */
    @Override
    default HolderHttpRequest setHeader(String key, String value){
        headerHolder().set(key, value);
        return this;
    }

    /**
     * 提供便捷的设置header的方法，add方式
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    @Override
    default HolderHttpRequest addHeader(String key, String value, String... values){
        headerHolder().add(key, value, values);
        return this;
    }

    /**
     * 提供便捷的设置header的方法
     * @param headers 多个header
     * @return this
     */
    @Override
    default HolderHttpRequest setHeaders(MultiValueMap<String, String> headers){
        headerHolder().set(headers);
        return this;
    }

    /**
     * 提供便捷的设置header的方法
     * @param headers 多个header
     * @return this
     */
    @Override
    default HolderHttpRequest setHeaders(Map<String, String> headers){
        headerHolder().set(headers);
        return this;
    }

    /**
     * SSL相关设置的处理器
     * @return SSLHolder must not be null
     */
    SSLHolder sslHolder();

    /**
     * 获取设置的header
     * @return 多值header
     */
    @Override
    default MultiValueMap<String, String> getHeaders() {
        return headerHolder().get();
    }

    /**
     * 获取设置的 HostNameVerifier
     * @return HostnameVerifier
     */
    @Override
    default HostnameVerifier getHostnameVerifier() {
        return sslHolder().getHostnameVerifier();
    }
    /**
     * 设置 HostNameVerifier
     * @param hostnameVerifier HostNameVerifier
     * @return this
     */
    @Override
    default HolderHttpRequest setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        sslHolder().setHostnameVerifier(hostnameVerifier);
        return this;
    }
    /**
     * 获取设置的SSLContext
     * @return SSLContext
     */
    @Override
    default SSLContext getSslContext() {
        return sslHolder().getSslContext();
    }
    /**
     * 设置SSLContext
     * @param sslContext SSLContext
     * @return this
     */
    @Override
    default HolderHttpRequest setSslContext(SSLContext sslContext) {
        sslHolder().setSslContext(sslContext);
        return this;
    }
    /**
     * 获取设置的SSLSocketFactory
     * @return SSLSocketFactory
     */
    @Override
    default SSLSocketFactory getSslSocketFactory() {
        return sslHolder().getSslSocketFactory();
    }
    /**
     * 获取设置的X509TrustManager
     * @return X509TrustManager
     */
    @Override
    default X509TrustManager getX509TrustManager() {
        return sslHolder().getX509TrustManager();
    }
    /**
     * 设置 X509TrustManager
     * @param x509TrustManager X509TrustManager
     * @return this
     */
    @Override
    default HolderHttpRequest setX509TrustManager(X509TrustManager x509TrustManager) {
        sslHolder().setX509TrustManager(x509TrustManager);
        return this;
    }

    /**
     * 获取属性处理器
     * @return must not be null
     */
    AttributeHolder attributeHolder();

    /**
     * 添加属性
     * @param key key
     * @param value value
     * @return this
     */
    @Override
    default HolderHttpRequest addAttribute(String key, Object value){
        attributeHolder().put(key , value);
        return this;
    }

    /**
     * 获取设置的属性
     * @return 属性map
     */
    @Override
    default Map<String , Object> getAttributes(){
        return attributeHolder().getMap();
    }
}
