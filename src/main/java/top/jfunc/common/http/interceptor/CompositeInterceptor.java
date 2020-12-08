package top.jfunc.common.http.interceptor;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.utils.ArrayUtil;
import top.jfunc.common.utils.CollectionUtil;

import java.io.IOException;
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
        if(ArrayUtil.isNotEmpty(interceptors)){
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
        return CollectionUtil.isNotEmpty(getInterceptors());
    }

    private void init(){
        if(null == this.interceptors){
            this.interceptors = new ArrayList<>(2);
        }
    }

    @Override
    public HttpRequest onBefore(HttpRequest httpRequest) throws IOException {
        HttpRequest temp = httpRequest;
        //循环执行拦截器代码
        if (CollectionUtil.isNotEmpty(getInterceptors())) {
            ///this.interceptors.forEach(executeInterceptor -> executeInterceptor.onBefore(httpRequest));
            for (Interceptor interceptor : getInterceptors()) {
                temp = interceptor.onBefore(temp);
            }
        }
        return temp;
    }

    @Override
    public ClientHttpResponse onBeforeReturn(HttpRequest httpRequest, ClientHttpResponse clientHttpResponse) throws IOException {
        ClientHttpResponse temp = clientHttpResponse;
        //逆序循环执行拦截器代码
        List<Interceptor> interceptors = getInterceptors();
        if (CollectionUtil.isNotEmpty(interceptors)) {
            for (int i = interceptors.size() - 1; i >= 0; i--) {
                temp = interceptors.get(i).onBeforeReturn(httpRequest, clientHttpResponse);
            }
        }
        return temp;
    }

    @Override
    public void onError(HttpRequest httpRequest, Exception exception) {
        //循环执行拦截器代码
        List<Interceptor> interceptors = getInterceptors();
        if (CollectionUtil.isNotEmpty(interceptors)) {
            getInterceptors().forEach(executeInterceptor -> executeInterceptor.onError(httpRequest, exception));
        }
    }

    @Override
    public void onFinally(HttpRequest httpRequest) {
        //逆序循环执行拦截器代码
        List<Interceptor> interceptors = getInterceptors();
        if (CollectionUtil.isNotEmpty(interceptors)) {
            for (int i = interceptors.size() - 1; i >= 0; i--) {
                interceptors.get(i).onFinally(httpRequest);
            }
        }
    }
}
