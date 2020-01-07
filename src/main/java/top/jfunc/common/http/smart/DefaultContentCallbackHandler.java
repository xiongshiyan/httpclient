package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultContentCallbackHandler<CC> implements ContentCallbackHandler<CC> {
    @Override
    public void handle(CC cc , ContentCallback<CC> contentCallback, HttpRequest httpRequest, Method method) throws IOException {
        if(null != contentCallback && method.hasContent()){
            contentCallback.doWriteWith(cc);
        }
    }
}
