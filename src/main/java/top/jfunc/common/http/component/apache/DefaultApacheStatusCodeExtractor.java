package top.jfunc.common.http.component.apache;

import org.apache.http.HttpResponse;
import top.jfunc.common.http.component.StatusCodeExtractor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheStatusCodeExtractor implements StatusCodeExtractor<HttpResponse> {
    @Override
    public Integer extract(HttpResponse httpResponse, HttpRequest httpRequest) throws IOException {
        return httpResponse.getStatusLine().getStatusCode();
    }
}
