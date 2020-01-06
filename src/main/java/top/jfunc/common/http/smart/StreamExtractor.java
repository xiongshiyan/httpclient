package top.jfunc.common.http.smart;

import java.io.InputStream;

/**
 * 从请求响应中获取stream
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface StreamExtractor<S> extends ResponseExtractor<S , InputStream> {
}
