package top.jfunc.common.http.request.basic;

/**
 * @author xiongshiyan at 2019/7/5 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CommonRequest extends BaseHttpRequest<CommonRequest> {
    public CommonRequest(String url){
        super(url);
    }
    public CommonRequest(){
    }
    public static CommonRequest of(String url){
        return new CommonRequest(url);
    }
    public static CommonRequest of(){
        return new CommonRequest();
    }
}
