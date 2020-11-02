package top.jfunc.common.http.cookie;

import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.util.List;

/**
 * Cookie存储器，只管cookie的存储，解析由{@link CookieAccessor}负责
 * @author xiongshiyan at 2020/11/2 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface CookieStore {
    /**
     * 获取请求可以携带的cookie
     * @param httpRequest 请求的所有参数
     * @return maybe null if there is not cookie
     * @throws IOException IOException
     */
    List<Cookie> loadForRequest(HttpRequest httpRequest) throws IOException;

    /**
     * 从http响应中保存cookie
     * @param cookies 所有的cookie
     * @param httpRequest 请求的所有参数
     * @throws IOException IOException
     */
    void saveFromResponse(List<Cookie> cookies, HttpRequest httpRequest) throws IOException;
}
