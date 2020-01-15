package top.jfunc.common.http.request.basic;

/**
 * @author xiongshiyan at 2019/7/8 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class GetRequest extends CommonRequest {
    public GetRequest(String url){
        super(url);
    }
    public GetRequest(){
    }
    public static GetRequest of(String url){
        return new GetRequest(url);
    }
    public static GetRequest of(){
        return new GetRequest();
    }
}
