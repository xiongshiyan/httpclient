package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.basic.HttpClient;

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
     * @param config config
     */
    @Override
    SmartHttpClient setConfig(Config config);

    /**
     * GET方法
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    Response get(Request request) throws IOException;
    /**
     * POST方法
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    Response post(Request request) throws IOException;

    Response httpMethod(Request request, Method method) throws IOException;

    /**
     * 下载为字节数组
     * @param request 请求参数
     * @return byte[]
     * @throws IOException IOException
     */
    byte[] getAsBytes(Request request) throws IOException;

    /**
     * 下载文件
     * @param request 请求参数
     * @return File 下载的文件
     * @throws IOException IOException
     */
    File getAsFile(Request request) throws IOException;

    /**
     * 文件上传
     * @param request 请求参数
     * @return Response
     * @throws IOException IOException
     */
    Response upload(Request request) throws IOException;

    /**
     * 对请求参数拦截处理 , 比如统一添加header , 参数加密 , 默认不处理
     * @param request Request
     * @return Request
     */
    default Request beforeTemplate(Request request){
        return Objects.requireNonNull(request);
    }

    /**
     * 对返回结果拦截处理 , 比如统一解密 , 默认不处理
     * @param request Request
     * @param response Response
     * @return Response
     * @throws IOException IOException
     */
    default Response afterTemplate(Request request, Response response) throws IOException{
        return response;
    }
}
