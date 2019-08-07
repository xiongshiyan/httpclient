package top.jfunc.common.http.holderrequest.impl;

import java.net.URL;

/**
 * get请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class HolderGetRequest extends BaseHolderHttpRequest<HolderGetRequest> {
    public HolderGetRequest(String url){
        super(url);
    }
    public HolderGetRequest(URL url){
        super(url);
    }
    public HolderGetRequest(){
    }
    public static HolderGetRequest of(){
        return new HolderGetRequest();
    }
    public static HolderGetRequest of(URL url){
        return new HolderGetRequest(url);
    }

    public static HolderGetRequest of(String url){
        return new HolderGetRequest(url);
    }
}
