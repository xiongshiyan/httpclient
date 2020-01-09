package top.jfunc.common.http.smart.apache;

import org.apache.http.HttpEntityEnclosingRequest;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.smart.AbstractBodyContentCallbackCreator;

import java.io.IOException;

import static top.jfunc.common.http.util.ApacheUtil.setRequestBody;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheBodyContentCallbackCreator extends AbstractBodyContentCallbackCreator<HttpEntityEnclosingRequest> {
    @Override
    public ContentCallback<HttpEntityEnclosingRequest> create(Method method, String body, String bodyCharset, String contentType) throws IOException {
        return request -> setRequestBody(request , body , bodyCharset);
    }
}
