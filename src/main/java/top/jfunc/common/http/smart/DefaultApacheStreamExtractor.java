package top.jfunc.common.http.smart;

import org.apache.http.HttpEntity;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.ApacheUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheStreamExtractor implements StreamExtractor<HttpEntity> {
    @Override
    public InputStream extract(HttpEntity httpEntity, HttpRequest httpRequest , String completedUrl) throws IOException {
        return ApacheUtil.getStreamFrom(httpEntity, httpRequest.isIgnoreResponseBody());
    }
}
