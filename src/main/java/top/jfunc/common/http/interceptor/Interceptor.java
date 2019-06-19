package top.jfunc.common.http.interceptor;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.request.HttpRequest;

/**
 * 执行拦截器，begin、returnValue、exception、finally这几个节点
 * @see top.jfunc.common.http.smart.SmartHttpTemplate
 * 典型的就是记录日志
 * @author xiongshiyan
 */
public interface Interceptor {
    /**
     * 执行之前拦截 before
     * @param httpRequest HttpRequest
     * @param method 请求方法
     */
    void onBefore(HttpRequest httpRequest, Method method);

    /**
     * 执行之后拦截 afterReturn
     * @param httpRequest HttpRequest
     * @param returnValue 返回的值
     */
    void onAfterReturn(HttpRequest httpRequest, Object returnValue);

    /**
     * 发生异常的时候 exception
     * @param httpRequest HttpRequest
     * @param exception Exception
     */
    void onError(HttpRequest httpRequest, Exception exception);

    /**
     * finally执行 finally
     * @param httpRequest HttpRequest
     */
    void onAfter(HttpRequest httpRequest);
}
