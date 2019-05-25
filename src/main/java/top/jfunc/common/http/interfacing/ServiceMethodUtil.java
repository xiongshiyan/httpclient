package top.jfunc.common.http.interfacing;

import java.lang.reflect.Method;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
class ServiceMethodUtil {
    static ServiceMethod<Object> parseAnnotations(JFuncHttp jfuncHttp , Method method){
        HttpRequestFactory httpRequestFactory = new HttpRequestFactory(method);
        return new HttpServiceMethod(jfuncHttp.getSmartHttpClient() , httpRequestFactory);
    }
}
