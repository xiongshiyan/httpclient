package top.jfunc.common.http.interfacing;

import top.jfunc.common.http.base.Method;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.smart.SmartHttpClient;

import java.io.IOException;

/**
 * 一个方法对应一个HttpServiceMethod，也就相应的对应一个HttpRequestFactory
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
class HttpServiceMethod implements ServiceMethod<Object> {
    private final SmartHttpClient smartHttpClient;
    private final RequestFactory requestFactory;

    public HttpServiceMethod(SmartHttpClient smartHttpClient, java.lang.reflect.Method method) {
        this.smartHttpClient = smartHttpClient;
        this.requestFactory = new HttpRequestFactory(method, smartHttpClient.getConfig());
    }

    @Override
    public Object invoke(Object[] args) throws IOException {
        HttpRequest httpRequest = requestFactory.httpRequest(args);
        Method httpMethod = requestFactory.getHttpMethod();

        if(Method.POST == httpMethod){
            if(httpRequest instanceof UploadRequest){
                //文件上传
                return smartHttpClient.upload((UploadRequest)httpRequest);
            }else {
                //一般POST
                return smartHttpClient.post((StringBodyRequest) httpRequest);
            }
        }else if(Method.GET == httpMethod){
                //一般GET
                return smartHttpClient.get(httpRequest);
        }else {
            //其他方法Http请求
            return smartHttpClient.http(httpRequest , httpMethod);
        }
    }
}
