package top.jfunc.common.http.smart;

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
     * @param completedUrl completedUrl计算后的真实的URL {@link top.jfunc.common.http.base.Config#handleUrlIfNecessary(String, Map, MultiValueMap, String)}
     * @return HttpURLConnection
     * @throws IOException IOException
     */
    C create(HttpRequest httpRequest, Method method , String completedUrl) throws IOException;
}
