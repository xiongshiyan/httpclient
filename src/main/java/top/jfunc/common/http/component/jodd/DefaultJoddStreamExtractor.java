package top.jfunc.common.http.component.jodd;

import jodd.http.HttpResponse;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.component.StreamExtractor;
import top.jfunc.common.http.util.JoddUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJoddStreamExtractor implements StreamExtractor<HttpResponse> {
    @Override
    public InputStream extract(HttpResponse httpResponse, HttpRequest httpRequest) throws IOException {
        return JoddUtil.getStreamFrom(httpResponse , httpRequest.isIgnoreResponseBody());
    }
}
