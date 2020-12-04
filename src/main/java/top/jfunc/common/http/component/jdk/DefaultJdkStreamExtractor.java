package top.jfunc.common.http.component.jdk;

import top.jfunc.common.http.component.AbstractStreamExtractor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static top.jfunc.common.http.base.HttpStatus.HTTP_BAD_REQUEST;
import static top.jfunc.common.http.base.HttpStatus.HTTP_OK;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJdkStreamExtractor extends AbstractStreamExtractor<HttpURLConnection> {
    /**
     * 注释的是Spring的方式，本方法在error的时候兼容性更好
     * 200(包含)-400(不包含)之间的响应码，可以调用{@link HttpURLConnection#getInputStream()}，
     * 否则只能调用{@link HttpURLConnection#getErrorStream()}
     */
    @Override
    public InputStream doExtract(HttpURLConnection connection, HttpRequest httpRequest) throws IOException {
        ///
        /*InputStream errorStream = connection.getErrorStream();
        return null != errorStream ? errorStream : connection.getInputStream();*/

        int statusCode = connection.getResponseCode();
        boolean hasInputStream = statusCode >= HTTP_OK && statusCode < HTTP_BAD_REQUEST;
        return hasInputStream ? connection.getInputStream() : connection.getErrorStream();
    }

    public static void main(String[] args) {

    }
}
