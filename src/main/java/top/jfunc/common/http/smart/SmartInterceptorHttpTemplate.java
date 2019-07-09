package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.util.Objects;

/**
 * @author xiongshiyan at 2019/4/2 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface SmartInterceptorHttpTemplate<C> extends SmartHttpTemplate<C> {

    /**
     * 实现父类，添加之前之后的拦截
     */
    @Override
    default <R> R template(HttpRequest httpRequest, Method method, ContentCallback<C> contentCallback, ResultCallback<R> resultCallback) throws IOException {
        HttpRequest request = beforeTemplate(httpRequest);
        R response = doTemplate(request, method, contentCallback, resultCallback);
        return afterTemplate(request , response);
    }

    /**
     * 使用Request来放请求数据
     * @param httpRequest 请求参数
     * @param method 请求方法
     * @param contentCallback 内容处理器，处理文本或者文件上传
     * @param resultCallback 结果处理器
     * @throws IOException IOException
     * @return <R> R
     */
    <R> R  doTemplate(HttpRequest httpRequest, Method method, ContentCallback<C> contentCallback, ResultCallback<R> resultCallback) throws IOException;


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
    default <R> R afterTemplate(HttpRequest request, R response) throws IOException{
        return response;
    }
}
