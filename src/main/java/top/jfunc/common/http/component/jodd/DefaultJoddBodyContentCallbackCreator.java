package top.jfunc.common.http.component.jodd;

import jodd.http.HttpRequest;
import top.jfunc.common.http.base.MediaType;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.AbstractBodyContentCallbackCreator;
import top.jfunc.common.http.request.StringBodyRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJoddBodyContentCallbackCreator extends AbstractBodyContentCallbackCreator<HttpRequest> {
    @Override
    protected ContentCallback<HttpRequest> doCreate(StringBodyRequest stringBodyRequest) throws IOException {
        String contentType = stringBodyRequest.getContentType();
        String bodyCharset = stringBodyRequest.calculateBodyCharset();
        String type = null == contentType ?
                MediaType.APPLICATION_JSON.withCharset(bodyCharset).toString() : contentType;
        return httpRequest -> httpRequest.bodyText(stringBodyRequest.getBody() , type, bodyCharset);
    }
}
