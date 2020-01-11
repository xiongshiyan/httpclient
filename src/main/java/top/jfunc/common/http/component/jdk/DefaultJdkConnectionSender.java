package top.jfunc.common.http.component.jdk;

import top.jfunc.common.http.component.RequestSender;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJdkConnectionSender implements RequestSender<HttpURLConnection , HttpURLConnection> {
    @Override
    public HttpURLConnection send(HttpURLConnection connection , HttpRequest httpRequest) throws IOException{
        connection.connect();
        return connection;
    }
}
