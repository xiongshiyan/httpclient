package top.jfunc.common.http.component.jdk;

import top.jfunc.common.http.component.AbstractStreamExtractor;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.NativeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJdkStreamExtractor extends AbstractStreamExtractor<HttpURLConnection> {
    @Override
    public InputStream doExtract(HttpURLConnection connection, HttpRequest httpRequest) throws IOException {
        return NativeUtil.getStreamFrom(connection, connection.getResponseCode());
    }
}
