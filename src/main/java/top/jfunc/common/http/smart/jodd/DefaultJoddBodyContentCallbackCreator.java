package top.jfunc.common.http.smart.jodd;

import jodd.http.HttpRequest;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.smart.AbstractBodyContentCallbackCreator;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJoddBodyContentCallbackCreator extends AbstractBodyContentCallbackCreator<HttpRequest> {
    @Override
    public ContentCallback<HttpRequest> create(Method method, String body, String bodyCharset, String contentType) throws IOException {
        String type = null == contentType ?
                MediaType.APPLICATIPON_JSON.withCharset(bodyCharset).toString() : contentType;
        return httpRequest -> httpRequest.bodyText(body , type, bodyCharset);
    }
}
