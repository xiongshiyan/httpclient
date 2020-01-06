package top.jfunc.common.http.smart;

import top.jfunc.common.http.smart.RequestSender;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultHttpURLConnectionSender implements RequestSender<HttpURLConnection , HttpURLConnection> {
    @Override
    public HttpURLConnection send(HttpURLConnection connection) throws IOException{
        connection.connect();
        return connection;
    }
}
