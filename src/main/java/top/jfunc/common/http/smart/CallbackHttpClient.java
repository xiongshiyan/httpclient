package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.HttpClient;
import top.jfunc.common.http.request.*;

import java.io.IOException;

/**
 * 提供对返回值的全面处理{@linkplain ResultCallback}
 * @author xiongshiyan at 2019/11/10
 * @since 1.1.10
 */
public interface CallbackHttpClient extends HttpClient {
    /**
     * GET方法，用于获取某个资源
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    <R> R get(HttpRequest request, ResultCallback<R> resultCallback) throws IOException;

    /**
     * POST方法，用于新增
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    <R> R post(StringBodyRequest request, ResultCallback<R> resultCallback) throws IOException;

    /**
     * POST方法，对form表单的语义化支持
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    default <R> R form(FormRequest request, ResultCallback<R> resultCallback) throws IOException{
        return post(request , resultCallback);
    }

    /**
     * 下载文件
     * @param request 请求参数
     * @return File 下载的文件
     * @throws IOException IOException
     */
    default <R> R download(DownloadRequest request, ResultCallback<R> resultCallback) throws IOException{
        return get(request , resultCallback);
    }

    /**
     * 文件上传
     * @param request 请求参数
     * @return Response
     * @throws IOException IOException
     */
    <R> R upload(UploadRequest request, ResultCallback<R> resultCallback) throws IOException;

    /**
     * 接口对其他http方法的支持
     * @param httpRequest Request
     * @param method Method
     * @param resultCallback resultCallback对结果的处理
     * @return <R>R
     * @throws IOException IOException
     */
    <R> R http(HttpRequest httpRequest, Method method, ResultCallback<R> resultCallback) throws IOException;

    /**
     * HEAD方法，一般返回某个接口的响应头，而没有响应体，用于探测Content-Length等信息
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @see Method#HEAD
     * @param httpRequest 请求参数
     * @return 一般只有请求头，即使有body也应该忽略
     * @throws IOException IOException
     */
    <R> R head(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException;

    /**
     * OPTIONS方法
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @see Method#OPTIONS
    access-control-allow-credentials →true
    access-control-allow-headers →Origin,X-Requested-With,Content-Type,Accept,Authorization,sourcetype,token
    access-control-allow-methods →POST,GET,PUT,OPTIONS,DELETE
    access-control-allow-origin →https://ossh5.palmte.cn
    access-control-max-age →3600
    connection →keep-alive
    content-length →0, 0
    content-type →application/octet-stream, text/plain
    date →Thu, 01 Aug 2019 06:29:43 GMT
    server →nginx
     * @param httpRequest 请求参数
     * @return 一般只有请求头，即使有body也应该忽略
     * @throws IOException IOException
     */
    <R> R options(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException;

    /**
     * PUT方法，用于更新
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @see SmartHttpClient#post(StringBodyRequest)
     * @see Method#PUT
     * @param httpRequest 请求参数
     * @return 响应
     * @throws IOException IOException
     */
    <R> R put(StringBodyRequest httpRequest, ResultCallback<R> resultCallback) throws IOException;

    /**
     * PATCH方法，用于部分更新
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @see SmartHttpClient#post(StringBodyRequest)
     * @see Method#PATCH
     * @param httpRequest 请求参数
     * @return 响应
     * @throws IOException IOException
     */
    <R> R patch(StringBodyRequest httpRequest, ResultCallback<R> resultCallback) throws IOException;

    /**
     * DELETE方法，用于删除某个资源
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @see Method#DELETE
     * @param httpRequest 请求参数
     * @return 响应
     * @throws IOException IOException
     */
    <R> R delete(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException;

    /**
     * TRACE方法，一般用于调试，在服务器支持的情况下会返回请求的头和body
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @see Method#TRACE
     * @param httpRequest 请求参数
     * @return 响应
     * @throws IOException IOException
     */
    <R> R trace(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException;
}
