package top.jfunc.common.http.request.impl;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.handler.ToString;
import top.jfunc.common.http.base.handler.ToStringHandler;
import top.jfunc.common.http.request.StringBodyRequest;

import java.util.Objects;

/**
 * Post请求，包含请求体
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class PostBodyRequest extends BaseRequest<PostBodyRequest> implements StringBodyRequest {

    public PostBodyRequest(String url){
        super(url);
    }
    public PostBodyRequest(){}

    public static PostBodyRequest of(String url){
        return new PostBodyRequest(url);
    }
    public static PostBodyRequest of(String url , String body , String contentType){
        PostBodyRequest postRequest = new PostBodyRequest(url);
        postRequest.setBody(body, contentType);
        return postRequest;
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
    public PostBodyRequest setBody(String body) {
        this.body = body;
        return this;
    }
    public PostBodyRequest setBody(String body , String contentType) {
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
    public <T> PostBodyRequest setBody(T o , ToStringHandler<T> handler){
        ToStringHandler<T> stringHandler = Objects.requireNonNull(handler, "handler不能为空");
        this.body = stringHandler.toString(o);
        return this;
    }
    public PostBodyRequest setBodyT(Object o , ToString handler){
        ToString toString = Objects.requireNonNull(handler, "handler不能为空");
        this.body = toString.toString(o);
        return this;
    }
}
