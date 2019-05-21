package top.jfunc.common.http.request.impl;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.handler.ToString;
import top.jfunc.common.http.base.handler.ToStringHandler;
import top.jfunc.common.http.request.StringBodyRequest;

import java.util.Objects;

/**
 * 通用的StringBody请求
 * @author xiongshiyan at 2019/5/21 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CommonBodyRequest extends BaseRequest<CommonBodyRequest> implements StringBodyRequest {
    public CommonBodyRequest(String url){
        super(url);
    }
    public static CommonBodyRequest of(String url){
        return new CommonBodyRequest(url);
    }
    public static CommonBodyRequest of(String url , String body , String contentType){
        CommonBodyRequest commonBodyRequest = new CommonBodyRequest(url);
        commonBodyRequest.setBody(body, contentType);
        return commonBodyRequest;
    }
    /**
     * 针对POST等存在
     * @see Method#hasContent()
     */
    private String body;

    @Override
    public String getBody() {
        return this.body;
    }

    /**
     * 设置body,最好是调用{@link this#setBody(String, String)}同时设置Content-Type
     */
    public CommonBodyRequest setBody(String body) {
        this.body = body;
        return this;
    }
    public CommonBodyRequest setBody(String body , String contentType) {
        this.body = body;
        setContentType(contentType);
        return this;
    }

    /**
     * 直接传输一个Java对象可以使用该方法
     * @param o Java对象
     * @param handler 将Java对象转换为String的策略接口
     * @return this
     */
    public <T> CommonBodyRequest setBody(T o , ToStringHandler<T> handler){
        ToStringHandler<T> stringHandler = Objects.requireNonNull(handler, "handler不能为空");
        this.body = stringHandler.toString(o);
        return this;
    }
    public CommonBodyRequest setBodyT(Object o , ToString handler){
        ToString toString = Objects.requireNonNull(handler, "handler不能为空");
        this.body = toString.toString(o);
        return this;
    }
}
