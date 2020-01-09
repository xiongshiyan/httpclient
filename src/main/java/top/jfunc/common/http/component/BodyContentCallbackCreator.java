package top.jfunc.common.http.component;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface BodyContentCallbackCreator<CC> extends ContentCallbackCreator<CC>{
    /**
     * 创建body设置器(为{@link top.jfunc.common.http.basic.HttpClient}体系使用)
     * @param method Method
     * @param body body
     * @param bodyCharset bodyCharset
     * @param contentType contentType
     * @return ContentCallback<CC>
     * @throws IOException IOException
     */
    ContentCallback<CC> create(Method method, String body, String bodyCharset, String contentType) throws IOException;
}
