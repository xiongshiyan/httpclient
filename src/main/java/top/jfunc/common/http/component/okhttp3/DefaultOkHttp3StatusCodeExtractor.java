package top.jfunc.common.http.component.okhttp3;

import okhttp3.Response;
import top.jfunc.common.http.component.StatusCodeExtractor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3StatusCodeExtractor implements StatusCodeExtractor<Response> {
    @Override
    public Integer extract(Response response, HttpRequest httpRequest) throws IOException {
        return response.code();
    }
}
