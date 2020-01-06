package top.jfunc.common.http.smart;

import jodd.http.HttpResponse;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.StreamExtractor;
import top.jfunc.common.http.util.JoddUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJoddStreamExtractor implements StreamExtractor<HttpResponse> {
    @Override
    public InputStream extract(HttpResponse httpResponse, HttpRequest httpRequest , String completedUrl) throws IOException {
        return JoddUtil.getStreamFrom(httpResponse , httpRequest.isIgnoreResponseBody());
    }
}
