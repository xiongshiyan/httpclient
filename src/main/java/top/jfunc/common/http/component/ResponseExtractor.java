package top.jfunc.common.http.component;

import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface ResponseExtractor<S , R>{
    /**
     * 从请求响应中获取信息
     * @param s 请求响应
     * @param httpRequest HttpRequest
     * @return R
     * @throws IOException IOException
     */
    R extract(S s, HttpRequest httpRequest) throws IOException;
}
