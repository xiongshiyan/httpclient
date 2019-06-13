package top.jfunc.common.http.request.impl;

import top.jfunc.common.http.request.StringBodyRequest;

/**
 * Post请求，包含请求体
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class PostBodyRequest extends CommonBodyRequest<PostBodyRequest> implements StringBodyRequest<PostBodyRequest> {
    public PostBodyRequest(String url){
        super(url);
    }
    public static PostBodyRequest of(String url){
        return new PostBodyRequest(url);
    }
    public static PostBodyRequest of(String url , String body , String contentType){
        PostBodyRequest postRequest = new PostBodyRequest(url);
        postRequest.setBody(body, contentType);
        return postRequest;
    }
}
