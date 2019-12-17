package top.jfunc.common.http.basic;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * http请求的模板方法接口
 * @author xiongshiyan at 2018/6/19
 */
public interface HttpTemplate<C>{
    /**
     * http请求的模板方法
     *
     * @param url URL
     * @param method 请求方法
     * @param contentType 请求体MIME类型
     * @param contentCallback 处理请求体的
     * @param headers headers
     * @param connectTimeout 连接超时时间
     * @param readTimeout 读取超时时间
     * @param resultCharset 结果字符集
     * @param includeHeaders 是否结果包含header
     * @param resultCallback 结果处理器
     * @param <R> 处理的结果
     * @return 处理的结果
     * @throws IOException IOException
     */
    <R> R template(String url, Method method, String contentType, ContentCallback<C> contentCallback,
                   MultiValueMap<String, String> headers, int connectTimeout, int readTimeout,
                   String resultCharset, boolean includeHeaders, ResultCallback<R> resultCallback) throws IOException;
}
