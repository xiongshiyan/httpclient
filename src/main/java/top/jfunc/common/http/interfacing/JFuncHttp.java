package top.jfunc.common.http.interfacing;

import top.jfunc.common.http.smart.SmartHttpClient;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参照retrofit2的做法，直接http请求接口化
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class JFuncHttp {
    private final Map<Method, ServiceMethod<?>> serviceMethodCache = new ConcurrentHashMap<>();
    private SmartHttpClient smartHttpClient;
    /**
     * Create an implementation of the API endpoints defined by the {@code httpService} interface.
     * @param httpService Http接口
     * @param <T> 实际返回的代理实现
     */
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> httpService) {
        validateInterface(httpService);
        return (T) Proxy.newProxyInstance(httpService.getClassLoader(), new Class<?>[] { httpService },
                new InvocationHandler() {
                    private final Object[] emptyArgs = new Object[0];

                    @Override public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        if(method.isDefault()){
                            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                            constructor.setAccessible(true);
                            /* trusted */
                            return constructor.newInstance(httpService, -1 )
                                    .unreflectSpecial(method, httpService)
                                    .bindTo(proxy)
                                    .invokeWithArguments(args);
                        }
                        return loadServiceMethod(method).invoke(args != null ? args : emptyArgs);
                    }
                });
    }

    private <T> void validateInterface(Class<T> httpService) {
        if (!httpService.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        if (httpService.getInterfaces().length > 0) {
            throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
        }
    }
    private ServiceMethod<?> loadServiceMethod(Method method) {
        ServiceMethod<?> result = serviceMethodCache.get(method);
        if (result != null) {
            return result;
        }

        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = ServiceMethodUtil.parseAnnotations(this , method);
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    public SmartHttpClient getSmartHttpClient() {
        return smartHttpClient;
    }

    public JFuncHttp setSmartHttpClient(SmartHttpClient smartHttpClient) {
        this.smartHttpClient = smartHttpClient;
        return this;
    }
}
