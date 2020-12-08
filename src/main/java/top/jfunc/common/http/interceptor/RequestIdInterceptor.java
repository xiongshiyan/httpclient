package top.jfunc.common.http.interceptor;

import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.CommonUtil;

import java.io.IOException;

/**
 * 用于生成一个RequestId放到请求头中,key默认为"RequestId"
 * @author xiongshiyan at 2020/1/20 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class RequestIdInterceptor extends InterceptorAdapter {
    public static final String REQUEST_ID = "RequestId";

    /**
     * header key
     */
    private String requestIdKey;

    public RequestIdInterceptor(String requestIdKey) {
        this.requestIdKey = requestIdKey;
    }

    public RequestIdInterceptor() {
        this.requestIdKey = REQUEST_ID;
    }

    @Override
    public HttpRequest onBefore(HttpRequest httpRequest) throws IOException {
        httpRequest.addHeader(getRequestIdKey() , generateRequestId(httpRequest));
        return super.onBefore(httpRequest);
    }

    protected String generateRequestId(HttpRequest httpRequest){
        return CommonUtil.randomString(16);
    }


    public String getRequestIdKey() {
        return requestIdKey;
    }

    public void setRequestIdKey(String requestIdKey) {
        this.requestIdKey = requestIdKey;
    }
}
