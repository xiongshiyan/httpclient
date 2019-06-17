package top.jfunc.common.http.interceptor;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.request.HttpRequest;

/**
 * 拦截器适配器
 * @author xiongshiyan at 2019/5/31 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class InterceptorAdapter implements Interceptor {
    @Override
    public void onBefore(HttpRequest httpRequest, Method method) {

    }

    @Override
    public void onAfterReturn(HttpRequest httpRequest, Object returnValue) {

    }

    @Override
    public void onError(HttpRequest httpRequest, Exception exception) {

    }

    @Override
    public void onAfter(HttpRequest httpRequest) {

    }
}
