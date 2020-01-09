package top.jfunc.common.http.component;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface ContentCallbackCreator<CC> {
    /**
     * 创建body设置器(为{@link top.jfunc.common.http.smart.SmartHttpClient}体系使用)
     * @param httpRequest HttpRequest
     * @param method Method
     * @return ContentCallback<CC>
     * @throws IOException IOException
     */
    ContentCallback<CC> create(HttpRequest httpRequest, Method method) throws IOException;
}
