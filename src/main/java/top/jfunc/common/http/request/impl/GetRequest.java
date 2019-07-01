package top.jfunc.common.http.request.impl;

/**
 * get请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class GetRequest extends BaseRequest<GetRequest> {
    public GetRequest(String url){
        super(url);
    }
    public static GetRequest of(String url){
        return new GetRequest(url);
    }
}
