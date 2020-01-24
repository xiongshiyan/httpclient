package top.jfunc.common.http.component.jdk;

import top.jfunc.common.http.component.StatusCodeExtractor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJdkStatusCodeExtractor implements StatusCodeExtractor<HttpURLConnection> {
    @Override
    public Integer extract(HttpURLConnection connection, HttpRequest httpRequest) throws IOException {
        return connection.getResponseCode();
    }
}
