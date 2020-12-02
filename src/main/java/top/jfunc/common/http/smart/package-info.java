package top.jfunc.common.http.smart;

/**
 * 本包定义了了Request-Response的{@link top.jfunc.common.http.smart.SmartHttpClient}接口，
 * 便于扩展功能，也更好使用。此包的类就是默认的实现，可以自己重写替换之
 * {@link top.jfunc.common.http.smart.Request}代表一个请求，{@link top.jfunc.common.http.smart.Response}代表面向用户的响应
 * {@link top.jfunc.common.http.smart.Request}可以使用
 * {@link top.jfunc.common.http.request.HttpRequest}、
 * {@link top.jfunc.common.http.request.StringBodyRequest}、
 * {@link top.jfunc.common.http.request.FormRequest}、
 * {@link top.jfunc.common.http.request.UploadRequest}
 * 这些语义更加明确的更加具体的实现来代替
 */