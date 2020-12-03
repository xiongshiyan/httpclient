package top.jfunc.common.http.template;

import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.response.ClientHttpResponse;

import java.io.IOException;
import java.util.Objects;

/**
 * @author xiongshiyan at 2019/4/2 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface TemplateInterceptor {
    /**
     * 对请求参数拦截处理 , 比如统一添加header , 参数加密 , 默认不处理
     * @param httpRequest Request
     * @return Request
     */
    default <T extends HttpRequest> T beforeTemplate(T httpRequest){
        return Objects.requireNonNull(httpRequest);
    }

    /**
     * 对返回结果拦截处理 , 比如统一解密 , 默认不处理
     * @param httpRequest Request
     * @param clientHttpResponse 框架得到的{@link ClientHttpResponse}
     * @return ClientHttpResponse 处理过后的响应
     * @throws IOException IOException
     */
    default ClientHttpResponse afterTemplate(HttpRequest httpRequest, ClientHttpResponse clientHttpResponse) throws IOException{
        return clientHttpResponse;
    }
}
