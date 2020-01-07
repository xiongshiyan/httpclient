package top.jfunc.common.http.smart;

import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.NativeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJdkStreamExtractor implements StreamExtractor<HttpURLConnection> {
    @Override
    public InputStream extract(HttpURLConnection connection, HttpRequest httpRequest , String completedUrl) throws IOException {
        return NativeUtil.getStreamFrom(connection, connection.getResponseCode(), httpRequest.isIgnoreResponseBody());
    }
}
