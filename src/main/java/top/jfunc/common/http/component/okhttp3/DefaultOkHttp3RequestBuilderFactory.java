package top.jfunc.common.http.component.okhttp3;

import okhttp3.Request;
import top.jfunc.common.http.component.AbstractRequesterFactory;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3RequestBuilderFactory extends AbstractRequesterFactory<Request.Builder> {
    @Override
    public Request.Builder doCreate(HttpRequest httpRequest) throws IOException {
        return new Request.Builder().url(httpRequest.getCompletedUrl());
    }
}
