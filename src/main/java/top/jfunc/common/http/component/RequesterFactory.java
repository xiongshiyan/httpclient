package top.jfunc.common.http.component;

import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface RequesterFactory<C> {
    /**
     * 创建请求处理器
     * @param httpRequest HttpRequest
     * @return HttpURLConnection
     * @throws IOException IOException
     */
    C create(HttpRequest httpRequest) throws IOException;
}
