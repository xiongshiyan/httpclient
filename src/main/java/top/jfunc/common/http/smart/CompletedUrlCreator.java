package top.jfunc.common.http.smart;

import top.jfunc.common.http.request.HttpRequest;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface CompletedUrlCreator {
    /**
     * 计算真正请求的URL
     * @param httpRequest HttpRequest
     * @return 真实请求的URL
     */
    String complete(HttpRequest httpRequest);
}
