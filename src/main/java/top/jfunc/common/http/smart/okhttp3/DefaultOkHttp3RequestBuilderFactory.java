package top.jfunc.common.http.smart.okhttp3;

import okhttp3.Request;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.RequesterFactory;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3RequestBuilderFactory implements RequesterFactory<Request.Builder> {
    @Override
    public Request.Builder create(HttpRequest httpRequest, Method method, String completedUrl) throws IOException {
        return new Request.Builder().url(completedUrl);
    }
}
