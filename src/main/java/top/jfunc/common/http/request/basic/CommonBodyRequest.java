package top.jfunc.common.http.request.basic;

import top.jfunc.common.http.request.MutableStringBodyRequest;
import top.jfunc.common.utils.CharsetUtil;

/**
 * 通用的StringBody请求
 * @author xiongshiyan at 2019/5/21 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CommonBodyRequest extends BaseHttpRequest<CommonBodyRequest> implements MutableStringBodyRequest {
    public CommonBodyRequest(String url){
        super(url);
    }
    public CommonBodyRequest(){
    }
    public static CommonBodyRequest of(){
        return new CommonBodyRequest();
    }
    public static CommonBodyRequest of(String url){
        return new CommonBodyRequest(url);
    }
    public static CommonBodyRequest of(String url , String body , String contentType){
        CommonBodyRequest commonBodyRequest = new CommonBodyRequest(url);
        commonBodyRequest.setBody(body, contentType);
        return commonBodyRequest;
    }
    private String body;
    private String bodyCharset = CharsetUtil.UTF_8;

    @Override
    public CommonBodyRequest setBody(String body) {
        this.body = body;
        return myself();
    }

    @Override
    public CommonBodyRequest setBody(String body, String contentType) {
        this.body = body;
        setContentType(contentType);
        return myself();
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public String getBodyCharset() {
        return bodyCharset;
    }

    @Override
    public CommonBodyRequest setBodyCharset(String bodyCharset) {
        this.bodyCharset = bodyCharset;
        return myself();
    }
}
