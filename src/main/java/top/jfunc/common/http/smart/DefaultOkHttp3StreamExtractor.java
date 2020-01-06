package top.jfunc.common.http.smart;

import okhttp3.Response;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.StreamExtractor;
import top.jfunc.common.http.util.OkHttp3Util;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3StreamExtractor implements StreamExtractor<Response> {
    @Override
    public InputStream extract(Response response, HttpRequest httpRequest , String completedUrl) throws IOException {
        return OkHttp3Util.getStreamFrom(response, httpRequest.isIgnoreResponseBody());
    }
}
