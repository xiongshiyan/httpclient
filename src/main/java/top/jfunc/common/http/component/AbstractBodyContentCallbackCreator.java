package top.jfunc.common.http.component;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractBodyContentCallbackCreator<CC> implements BodyContentCallbackCreator<CC> {

    @Override
    public ContentCallback<CC> create(HttpRequest httpRequest) throws IOException{
        StringBodyRequest stringBodyRequest = (StringBodyRequest) httpRequest;
        String body = stringBodyRequest.getBody();
        Config config = httpRequest.getConfig();
        String bodyCharset = config.calculateBodyCharset(stringBodyRequest.getBodyCharset() , stringBodyRequest.getContentType());

        return create(httpRequest.getMethod() , body , bodyCharset , stringBodyRequest.getContentType());
    }
}
