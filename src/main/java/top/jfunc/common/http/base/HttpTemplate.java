package top.jfunc.common.http.base;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.smart.Request;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.http.smart.SSLRequest;

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
                   ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout,
                   String resultCharset, boolean includeHeaders, ResultCallback<R> resultCallback) throws IOException;

    /**
     * 使用Request来放请求数据
     * @param request 请求参数
     * @param method 请求方法
     * @see SSLRequest
     * @param contentCallback 内容处理器，处理文本或者文件上传
     * @param resultCallback 结果处理器
     * @throws IOException IOException
     * @return <R> R
     */
    <R> R  template(Request request, Method method, ContentCallback<C> contentCallback, ResultCallback<R> resultCallback) throws IOException;

}
