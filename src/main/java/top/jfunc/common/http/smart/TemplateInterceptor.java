package top.jfunc.common.http.smart;

import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.util.Objects;

/**
 * @author xiongshiyan at 2019/4/2 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface TemplateInterceptor {
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
