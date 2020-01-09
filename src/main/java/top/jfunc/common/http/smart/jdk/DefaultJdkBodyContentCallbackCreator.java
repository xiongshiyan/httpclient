package top.jfunc.common.http.smart.jdk;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.smart.AbstractBodyContentCallbackCreator;
import top.jfunc.common.http.util.NativeUtil;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJdkBodyContentCallbackCreator extends AbstractBodyContentCallbackCreator<HttpURLConnection> {
    @Override
    public ContentCallback<HttpURLConnection> create(Method method, String body, String bodyCharset, String contentType) throws IOException {
        return connect -> NativeUtil.writeContent(connect , body , bodyCharset);
    }
}
