package top.jfunc.common.http.request.basic;

/**
 * Put请求，包含请求体
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class PutBodyRequest extends CommonBodyRequest {
    public PutBodyRequest(String url){
        super(url);
    }
    public static PutBodyRequest of(String url){
        return new PutBodyRequest(url);
    }
    public static PutBodyRequest of(String url , String body , String contentType){
        PutBodyRequest putBodyRequest = new PutBodyRequest(url);
        putBodyRequest.setBody(body, contentType);
        return putBodyRequest;
    }
}
