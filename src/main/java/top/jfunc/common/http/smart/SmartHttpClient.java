package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.HttpClient;
import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.holder.impl.HolderCommonRequest;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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
     * GET方法
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    Response get(HttpRequest request) throws IOException;
    /**
     * POST方法
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    Response post(StringBodyRequest request) throws IOException;

    /**
     * 接口对其他http方法的支持
     * @param httpRequest Request
     * @param method Method
     * @return Response
     * @throws IOException IOException
     */
    default Response http(HttpRequest httpRequest, Method method) throws IOException{
        Response response = http(httpRequest, method, Response::with);
        return afterTemplate(httpRequest , response);
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
     * 下载为字节数组
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
     * 文件上传
     * @param request 请求参数
     * @return Response
     * @throws IOException IOException
     */
    Response upload(UploadRequest request) throws IOException;

    /**
     * 对请求参数拦截处理 , 比如统一添加header , 参数加密 , 默认不处理
     * @param request Request
     * @return Request
     */
    default <T extends HttpRequest> T beforeTemplate(T request){
        return Objects.requireNonNull(request);
    }

    /**
     * 对返回结果拦截处理 , 比如统一解密 , 默认不处理
     * @param request Request
     * @param response Response
     * @return Response
     * @throws IOException IOException
     */
    default Response afterTemplate(HttpRequest request, Response response) throws IOException{
        if(request.isRedirectable() && response.needRedirect()){
            return get(HolderCommonRequest.of(response.getRedirectUrl()));
        }
        return response;
    }
}
