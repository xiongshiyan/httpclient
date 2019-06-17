package top.jfunc.common.http.interceptor;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.HttpRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 提供对多个拦截器的管理，可以新增拦截器，实际拦截执行是委托给各个具体的拦截器
 * @see top.jfunc.common.http.interceptor.Interceptor
 * @see top.jfunc.common.http.interceptor.InterceptorAdapter
 * @see Config#getCompositeInterceptor()
 * @author xiongshiyan at 2019/5/31 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CompositeInterceptor implements Interceptor {

    private List<Interceptor> interceptors;
    public CompositeInterceptor(){}
    public CompositeInterceptor(Interceptor... interceptors){
        init();
        this.interceptors.addAll(Arrays.asList(interceptors));
    }
    public CompositeInterceptor(List<Interceptor> interceptors){
        init();
        this.interceptors.addAll(interceptors);
    }

    public CompositeInterceptor setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
        return this;
    }

    /**
     * 添加一个拦截器
     */
    public CompositeInterceptor add(Interceptor interceptor){
        init();
        this.interceptors.add(interceptor);
        return this;
    }

    /**
     * 添加至少一个拦截器
     */
    public CompositeInterceptor add(Interceptor interceptor , Interceptor... interceptors){
        init();
        this.interceptors.add(interceptor);
        if(null != interceptors && interceptors.length > 0){
            this.interceptors.addAll(Arrays.asList(interceptors));
        }
        return this;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    /**
     * 判断是否有具体的拦截器
     */
    public boolean hasInterceptors(){
        return null != this.interceptors && !this.interceptors.isEmpty();
    }

    private void init(){
        if(null == this.interceptors){
            this.interceptors = new ArrayList<>(2);
        }
    }

    @Override
    public void onBefore(HttpRequest httpRequest, Method method) {
        //循环执行拦截器代码
        if (null != this.interceptors && !this.interceptors.isEmpty()) {
            this.interceptors.forEach(executeInterceptor -> executeInterceptor.onBefore(httpRequest, method));
        }
    }

    @Override
    public void onAfterReturn(HttpRequest httpRequest, Object returnValue) {
        //逆序循环执行拦截器代码
        if (null != this.interceptors && !this.interceptors.isEmpty()) {
            for (int i = this.interceptors.size() - 1; i >= 0; i--) {
                this.interceptors.get(i).onAfterReturn(httpRequest, returnValue);
            }
        }
    }

    @Override
    public void onError(HttpRequest httpRequest, Exception exception) {
        //循环执行拦截器代码
        if (null != this.interceptors && !this.interceptors.isEmpty()) {
            this.interceptors.forEach(executeInterceptor -> executeInterceptor.onError(httpRequest, exception));
        }
    }

    @Override
    public void onAfter(HttpRequest httpRequest) {
        //逆序循环执行拦截器代码
        if (null != this.interceptors && !this.interceptors.isEmpty()) {
            for (int i = this.interceptors.size() - 1; i >= 0; i--) {
                this.interceptors.get(i).onAfter(httpRequest);
            }
        }
    }
}
