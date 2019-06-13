package top.jfunc.common.http.request.impl;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.holder.BodyHolder;
import top.jfunc.common.http.holder.DefaultBodyHolder;
import top.jfunc.common.http.request.MutableStringBodyRequest;

/**
 * 通用的StringBody请求
 * @author xiongshiyan at 2019/5/21 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CommonBodyRequest<THIS extends CommonBodyRequest> extends BaseRequest<THIS> implements MutableStringBodyRequest<THIS> {
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
     * 针对POST等存在//private String body
     * @see Method#hasContent()
     */
    private BodyHolder bodyHolder = new DefaultBodyHolder();

    @Override
    public THIS setBody(String body , String contentType) {
        this.bodyHolder.setBody(body);
        setContentType(contentType);
        return myself();
    }

    @Override
    public BodyHolder bodyHolder() {
        return bodyHolder;
    }
}
