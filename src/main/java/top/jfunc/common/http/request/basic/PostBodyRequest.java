package top.jfunc.common.http.request.basic;

/**
 * Post请求，包含请求体
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class PostBodyRequest extends CommonBodyRequest {
    public PostBodyRequest(String url){
        super(url);
    }
    public PostBodyRequest(){
    }

    public static PostBodyRequest of(){
        return new PostBodyRequest();
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
