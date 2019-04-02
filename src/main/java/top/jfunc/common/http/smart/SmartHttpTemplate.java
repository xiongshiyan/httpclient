package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.HttpTemplate;
import top.jfunc.common.http.base.ResultCallback;

import java.io.IOException;

/**
 * @author xiongshiyan at 2019/4/2 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface SmartHttpTemplate<C> extends HttpTemplate<C>{
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
/*
    default <R> R  template(Request request, Method method, ContentCallback<C> contentCallback, ResultCallback<R> resultCallback) throws IOException{
        return template(request.getUrl() ,
                method ,
                request.getContentType() ,
                contentCallback ,
                request.getHeaders() ,
                request.getConnectionTimeout() ,
                request.getReadTimeout() ,
                request.getResultCharset() ,
                request.isIncludeHeaders() ,
                resultCallback);
    }
*/
}