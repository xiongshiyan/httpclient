package top.jfunc.common.http.interceptor;

import top.jfunc.common.http.request.HttpRequest;

/**
 * 条件拦截器，子类指定条件对哪些请求进行拦截
 * 针对不同的{@link top.jfunc.common.http.request.HttpRequest HttpRequest}的拦截
 * @author xiongshiyan at 2019/7/30 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractConditionInterceptor extends InterceptorAdapter {
    @Override
    public HttpRequest onBefore(HttpRequest httpRequest) {
        if(needIntercept(httpRequest)){
            return internalIntercept(httpRequest);
        }
        return super.onBefore(httpRequest);
    }

    /**
     * 指定对哪些请求进行拦截，like judge httpRequest instanceof StringBodyRequest
     * @param httpRequest HttpRequest
     * @return true if need
     */
    protected abstract boolean needIntercept(HttpRequest httpRequest);

    /**
     * 做真正拦截的事情，比如对body进行统一包裹一层
     * @param httpRequest HttpRequest
     * @return 修改后的HttpRequest
     */
    protected abstract HttpRequest internalIntercept(HttpRequest httpRequest);
}
