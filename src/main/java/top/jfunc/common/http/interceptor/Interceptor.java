package top.jfunc.common.http.interceptor;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.request.HttpRequest;

/**
 * 执行拦截器，之前拦截、之后拦截、异常拦截
 * 典型的就是记录日志
 * @author xiongshiyan
 */
public interface Interceptor {
    /**
     * 执行之前拦截
     * @param httpRequest HttpRequest
     * @param method 请求方法
     */
    void onBefore(HttpRequest httpRequest , Method method);

    /**
     * 执行之后拦截
     * @param httpRequest HttpRequest
     * @param returnValue 返回的值
     */
    void onAfterReturn(HttpRequest httpRequest , Object returnValue);

    /**
     * 发生异常的时候
     * @param httpRequest HttpRequest
     * @param exception Exception
     */
    void onError(HttpRequest httpRequest , Exception exception);
    /**
     * finally执行
     * @param httpRequest HttpRequest
     */
    void onAfter(HttpRequest httpRequest);
}
