package top.jfunc.common.http.holderrequest.impl;

/**
 * Post请求，包含请求体
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class HolderPostBodyRequest extends HolderCommonBodyRequest {
    public HolderPostBodyRequest(String url){
        super(url);
    }
    public static HolderPostBodyRequest of(String url){
        return new HolderPostBodyRequest(url);
    }
    public static HolderPostBodyRequest of(String url , String body , String contentType){
        HolderPostBodyRequest postRequest = new HolderPostBodyRequest(url);
        postRequest.setBody(body, contentType);
        return postRequest;
    }
}
