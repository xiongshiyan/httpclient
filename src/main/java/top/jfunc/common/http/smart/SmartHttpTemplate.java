package top.jfunc.common.http.smart;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2019/4/2 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface SmartHttpTemplate<CC> {
    /**
     * 使用Request来放请求数据
     * @param httpRequest 请求参数
     * @param contentCallback 内容处理器，处理文本或者文件上传
     * @param resultCallback 结果处理器
     * @throws IOException IOException
     * @return <R> R
     */
    <R> R  template(HttpRequest httpRequest, ContentCallback<CC> contentCallback, ResultCallback<R> resultCallback) throws IOException;
}
