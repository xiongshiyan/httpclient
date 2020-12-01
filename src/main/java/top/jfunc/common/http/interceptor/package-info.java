package top.jfunc.common.http.interceptor;

/**
 * 此包定义网络请求的拦截器，拦截接口 {@link top.jfunc.common.http.template.SmartHttpTemplate SmartHttpTemplate} 的
 * begin、returnValue、exception、finally这几个节点，{@link top.jfunc.common.http.interceptor.CompositeInterceptor CompositeInterceptor}
 * 可以管理多个拦截器，{@link top.jfunc.common.http.base.Config Config} 就是使用它来管理
 * @see top.jfunc.common.http.interceptor.Interceptor
 * @see top.jfunc.common.http.interceptor.CompositeInterceptor
 * @see top.jfunc.common.http.template.SmartHttpTemplate
 * @see top.jfunc.common.http.base.Config#getCompositeInterceptor()
 */