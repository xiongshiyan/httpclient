package top.jfunc.common.http.holderrequest.impl;

import java.net.URL;

/**
 * Put请求，包含请求体
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class HolderPutBodyRequest extends HolderCommonBodyRequest {
    public HolderPutBodyRequest(String url){
        super(url);
    }
    public HolderPutBodyRequest(URL url){
        super(url);
    }
    public HolderPutBodyRequest(){
    }
    public static HolderPutBodyRequest of(){
        return new HolderPutBodyRequest();
    }
    public static HolderPutBodyRequest of(URL url){
        return new HolderPutBodyRequest(url);
    }
    public static HolderPutBodyRequest of(String url){
        return new HolderPutBodyRequest(url);
    }
    public static HolderPutBodyRequest of(String url , String body , String contentType){
        HolderPutBodyRequest putBodyRequest = new HolderPutBodyRequest(url);
        putBodyRequest.setBody(body, contentType);
        return putBodyRequest;
    }
}
