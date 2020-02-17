package top.jfunc.common.http.holderrequest.impl;

import top.jfunc.common.http.holder.*;
import top.jfunc.common.http.holderrequest.HolderHttpRequest;
import top.jfunc.common.http.request.AbstractHttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.net.URL;
import java.util.Map;

/**
 * 基本请求参数实现:可用于无请求体如Get等的请求
 * T泛型为了变种的setter返回this便于链式调用
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class BaseHolderHttpRequest<THIS extends BaseHolderHttpRequest> extends AbstractHttpRequest<THIS> implements HolderHttpRequest {
    /**
     * 请求的URL
     */
    private UrlHolder urlHolder = new DefaultUrlHolder();
    /**
     * 请求头
     */
    private HeaderHolder headerHolder = new DefaultHeaderHolder();
    /**
     * SSL相关设置
     */
    private SSLHolder sslHolder = new DefaultSSLHolder();
    /**
     * 属性设置器
     */
    private AttributeHolder attributeHolder = new DefaultAttributeHolder();

    public BaseHolderHttpRequest(String url){this.urlHolder.setUrl(url);}
    public BaseHolderHttpRequest(URL url){this.urlHolder.setUrl(url);}
    public BaseHolderHttpRequest(){}

    @Override
    public THIS setUrl(String url) {
        this.urlHolder.setUrl(url);
        return myself();
    }
    @Override
    public THIS setUrl(URL url) {
        this.urlHolder.setUrl(url);
        return myself();
    }

    @Override
    public THIS addRouteParam(String key, String value) {
        routeParamHolder().put(key, value);
        return myself();
    }

    @Override
    public THIS setRouteParams(Map<String, String> routeParams) {
        routeParamHolder().setMap(routeParams);
        return myself();
    }

    @Override
    public THIS addQueryParam(String key, String value , String... values){
        queryParamHolder().addParam(key, value, values);
        return myself();
    }

    @Override
    public THIS setQueryParams(MultiValueMap<String, String> queryParams) {
        queryParamHolder().setParams(queryParams);
        return myself();
    }

    @Override
    public THIS setQueryParams(Map<String, String> queryParams) {
        queryParamHolder().setParams(queryParams);
        return myself();
    }

    @Override
    public HeaderHolder headerHolder() {
        return headerHolder;
    }

    @Override
    public THIS setHeader(String key, String value) {
        headerHolder.setHeader(key, value);
        return myself();
    }

    @Override
    public THIS addHeader(String key, String value , String... values){
        headerHolder().addHeader(key, value, values);
        return myself();
    }

    @Override
    public THIS setHeaders(Map<String, String> headers) {
        headerHolder().setHeaders(headers);
        return myself();
    }

    @Override
    public THIS setHeaders(MultiValueMap<String, String> headers) {
        headerHolder().setHeaders(headers);
        return myself();
    }

    @Override
    public UrlHolder urlHolder() {
        return urlHolder;
    }

    @Override
    public THIS urlHolder(UrlHolder urlHolder) {
        this.urlHolder = urlHolder;
        return myself();
    }

    @Override
    public SSLHolder sslHolder() {
        return sslHolder;
    }

    @Override
    public AttributeHolder attributeHolder() {
        return attributeHolder;
    }

    @Override
    public THIS addAttribute(String key, Object value) {
        this.attributeHolder.addAttribute(key, value);
        return myself();
    }
}
