package top.jfunc.common.http.component.okhttp3;

import okhttp3.Request;
import okhttp3.RequestBody;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.AbstractBodyContentCallbackCreator;

import java.io.IOException;

import static top.jfunc.common.http.util.OkHttp3Util.setRequestBody;
import static top.jfunc.common.http.util.OkHttp3Util.stringBody;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3BodyContentCallbackCreator extends AbstractBodyContentCallbackCreator<Request.Builder> {
    @Override
    public ContentCallback<Request.Builder> create(Method method, String body, String bodyCharset, String contentType) throws IOException {
        RequestBody stringBody = stringBody(body, bodyCharset, contentType);
        return builder -> setRequestBody(builder, method, stringBody);
    }
}
