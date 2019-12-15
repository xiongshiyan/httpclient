package top.jfunc.common.http.cookie;

import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.util.List;

/**
 * 处理Cookie的接口，想法来源于OkHttp3重构代码提出此接口
 * @author xiongshiyan
 */
public interface CookieJar {
    /**
     * 获取请求可以携带的cookie，其形式为key=value
     * @param completedUrl 请求的URI
     * @return cookies maybe null if there is not cookie
     * @throws IOException IOException
     */
    List<String> loadForRequest(String completedUrl) throws IOException;

    /**
     * 从http响应中保存cookie
     * @param completedUrl 请求的URI
     * @param responseHeaders 响应的headers，maybe null
     * @throws IOException IOException
     */
  void saveFromResponse(String completedUrl, MultiValueMap<String, String> responseHeaders) throws IOException;
}