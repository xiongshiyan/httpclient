package top.jfunc.common.http.template;
/**
 此包定义实现模板{@link top.jfunc.common.http.template.SmartHttpTemplate}
 及模板的拦截器{@link top.jfunc.common.http.template.TemplateInterceptor}在模板执行前后进行拦截

 模板方法{@link top.jfunc.common.http.template.SmartHttpTemplate#template(top.jfunc.common.http.request.HttpRequest, top.jfunc.common.http.base.ContentCallback, top.jfunc.common.http.response.ClientHttpResponseConverter)}
 参数封装一个http请求一般遵循的步骤
 1. {@link top.jfunc.common.http.request.HttpRequest}代表请求，是所有请求的公共父类
 2. {@link top.jfunc.common.http.base.ContentCallback}封装对body的处理
 3. {@link top.jfunc.common.http.response.ClientHttpResponseConverter}封装对结果的处理
 */