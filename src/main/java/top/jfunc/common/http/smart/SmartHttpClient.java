package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.HttpClient;
import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;

import java.io.File;
import java.io.IOException;

/**
 * @author xiongshiyan at 2017/12/9
 * 针对Http超时和各种错误码分别处理
 * @see HttpClient
 * 使用时，可以直接new实现类，也可以通过{@link top.jfunc.common.http.HttpUtil }获取，这样就不会与实现类绑定
 */
public interface SmartHttpClient extends HttpClient {
    /**
     * 设置全局默认配置,不调用就用系统设置的
     * @param config config
     */
    @Override
    void setConfig(Config config);

    /**
     * 获取全局配置
     * @return 全局配置的config
     */
    @Override
    Config getConfig();

    /**
     * GET方法，用于获取某个资源
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    Response get(HttpRequest request) throws IOException;

    /**
     * 下载为字节数组
     * 也可以使用{@link SmartHttpClient#get(HttpRequest)}得到，再根据{@link Response#asBytes()}达到相同的效果
     * @param request 请求参数
     * @return byte[]
     * @throws IOException IOException
     */
    byte[] getAsBytes(HttpRequest request) throws IOException;

    /**
     * 下载文件
     * @param request 请求参数
     * @return File 下载的文件
     * @throws IOException IOException
     */
    default File getAsFile(DownloadRequest request) throws IOException{
        return download(request);
    }

    /**
     * 下载文件
     * @param request 请求参数
     * @return File 下载的文件
     * @throws IOException IOException
     */
    File download(DownloadRequest request) throws IOException;

    /**
     * POST方法，用于新增
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    Response post(StringBodyRequest request) throws IOException;

    /**
     * 文件上传
     * @param request 请求参数
     * @return Response
     * @throws IOException IOException
     */
    Response upload(UploadRequest request) throws IOException;

    /**
     * 接口对其他http方法的支持
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @param httpRequest Request
     * @param method Method
     * @return Response
     * @throws IOException IOException
     */
    default Response http(HttpRequest httpRequest, Method method) throws IOException{
        return http(httpRequest, method, Response::with);
    }

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
    Response head(HttpRequest httpRequest) throws IOException;

    /**
     * OPTIONS方法
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @see Method#OPTIONS
     * access-control-allow-credentials →true
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
    Response options(HttpRequest httpRequest) throws IOException;

    /**
     * PUT方法，用于更新
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @see SmartHttpClient#post(StringBodyRequest)
     * @see Method#PUT
     * @param httpRequest 请求参数
     * @return 响应
     * @throws IOException IOException
     */
    Response put(StringBodyRequest httpRequest) throws IOException;

    /**
     * PUT方法，用于部分更新
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @see SmartHttpClient#post(StringBodyRequest)
     * @see Method#PATCH
     * @param httpRequest 请求参数
     * @return 响应
     * @throws IOException IOException
     */
    Response patch(StringBodyRequest httpRequest) throws IOException;

    /**
     * DELETE方法，用于删除某个资源
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @see Method#DELETE
     * @param httpRequest 请求参数
     * @return 响应
     * @throws IOException IOException
     */
    Response delete(HttpRequest httpRequest) throws IOException;

    /**
     * TRACE方法，一般用于调试，在服务器支持的情况下会返回请求的头和body
     * @see SmartHttpClient#http(HttpRequest, Method, ResultCallback)
     * @see Method#TRACE
     * @param httpRequest 请求参数
     * @return 响应
     * @throws IOException IOException
     */
    Response trace(HttpRequest httpRequest) throws IOException;
}
