package top.jfunc.common.http.component;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.util.Map;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface RequesterFactory<C> {
    /**
     * 创建请求处理器
     * @param httpRequest HttpRequest
     * @param method Method
     * @return HttpURLConnection
     * @throws IOException IOException
     */
    C create(HttpRequest httpRequest, Method method) throws IOException;
}
