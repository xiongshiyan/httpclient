package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.HttpClient;
import top.jfunc.common.http.request.*;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;

/**
 * @author xiongshiyan at 2017/12/9
 * 针对Http超时和各种错误码分别处理
 * @see HttpClient
 * 使用时，可以直接new实现类，也可以通过{@link top.jfunc.common.http.HttpUtil }获取，这样就不会与实现类绑定
 */
public interface SmartHttpClient extends ResultCallbackHttpClient {
    /**
     * @inheritDoc
     */
    @Override
    void setConfig(Config config);

    /**
     * @inheritDoc
     */
    @Override
    Config getConfig();

    /**
     * GET方法，用于获取某个资源
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    default Response get(HttpRequest request) throws IOException{
        return get(request, Response::with);
    }

    /**
     * 下载为字节数组
     * 也可以使用{@link SmartHttpClient#get(HttpRequest)}得到，再根据{@link Response#asBytes()}达到相同的效果
     * @param request 请求参数
     * @return byte[]
     * @throws IOException IOException
     */
    default byte[] getAsBytes(HttpRequest request) throws IOException{
        return get(request, (s, b, r, h) -> IoUtil.stream2Bytes(b));
    }

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
     * POST方法，用于新增
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    default Response post(StringBodyRequest request) throws IOException{
        return post(request , Response::with);
    }

    /**
     * POST方法，对form表单的语义化支持
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    default Response form(FormRequest request) throws IOException{
        return form(request , Response::with);
    }

    /**
     * 下载文件
     * @param request 请求参数
     * @return File 下载的文件
     * @throws IOException IOException
     */
    default File download(DownloadRequest request) throws IOException{
        return download(request , (s, b, r, h) -> IoUtil.copy2File(b, request.getFile()));
    }

    /**
     * 文件上传
     * @param request 请求参数
     * @return Response
     * @throws IOException IOException
     */
    default Response upload(UploadRequest request) throws IOException{
        return upload(request , Response::with);
    }

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

    default MultiValueMap<String , String> head(HttpRequest httpRequest) throws IOException{
        return head(httpRequest, ResultCallback::headers);
    }
    default MultiValueMap<String , String> options(HttpRequest httpRequest) throws IOException{
        return options(httpRequest, ResultCallback::headers);
    }
    default Response put(StringBodyRequest httpRequest) throws IOException{
        return put(httpRequest , Response::with);
    }
    default Response patch(StringBodyRequest httpRequest) throws IOException{
        return patch(httpRequest , Response::with);
    }
    default Response delete(HttpRequest httpRequest) throws IOException{
        return delete(httpRequest, Response::with);
    }
    default Response trace(HttpRequest httpRequest) throws IOException{
        return trace(httpRequest, Response::with);
    }
}
