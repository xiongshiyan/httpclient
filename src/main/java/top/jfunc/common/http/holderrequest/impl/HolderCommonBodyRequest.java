package top.jfunc.common.http.holderrequest.impl;

import top.jfunc.common.http.base.Method;
import top.jfunc.common.http.holder.BodyHolder;
import top.jfunc.common.http.holder.DefaultBodyHolder;
import top.jfunc.common.http.holderrequest.HolderMutableStringBodyRequest;

import java.net.URL;

/**
 * 通用的StringBody请求
 * @author xiongshiyan at 2019/5/21 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class HolderCommonBodyRequest extends BaseHolderHttpRequest<HolderCommonBodyRequest> implements HolderMutableStringBodyRequest {
    public HolderCommonBodyRequest(String url){
        super(url);
    }
    public HolderCommonBodyRequest(URL url){
        super(url);
    }
    public HolderCommonBodyRequest(){
    }

    public static HolderCommonBodyRequest of(){
        return new HolderCommonBodyRequest();
    }
    public static HolderCommonBodyRequest of(URL url){
        return new HolderCommonBodyRequest(url);
    }


    public static HolderCommonBodyRequest of(String url){
        return new HolderCommonBodyRequest(url);
    }
    public static HolderCommonBodyRequest of(String url , String body , String contentType){
        HolderCommonBodyRequest commonBodyRequest = new HolderCommonBodyRequest(url);
        commonBodyRequest.setBody(body, contentType);
        return commonBodyRequest;
    }
    /**
     * 针对POST等存在//private String body
     * @see Method#hasContent()
     */
    private BodyHolder bodyHolder = new DefaultBodyHolder();

    @Override
    public HolderCommonBodyRequest setBody(String body , String contentType) {
        this.bodyHolder.setBody(body);
        setContentType(contentType);
        return this;
    }

    @Override
    public BodyHolder bodyHolder() {
        return bodyHolder;
    }
}
