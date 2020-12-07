package top.jfunc.common.http.component.okhttp3;

import okhttp3.Request;
import okhttp3.RequestBody;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.AbstractBodyContentCallbackCreator;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.util.OkHttp3Util;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3BodyContentCallbackCreator extends AbstractBodyContentCallbackCreator<Request.Builder> {
    @Override
    protected ContentCallback<Request.Builder> doCreate(StringBodyRequest stringBodyRequest) throws IOException {
        RequestBody stringBody = OkHttp3Util.stringBody(stringBodyRequest.getBody(), stringBodyRequest.calculateBodyCharset(), stringBodyRequest.getContentType());
        return builder -> OkHttp3Util.setRequestBody(builder, stringBodyRequest.getMethod(), stringBody);
    }
}
