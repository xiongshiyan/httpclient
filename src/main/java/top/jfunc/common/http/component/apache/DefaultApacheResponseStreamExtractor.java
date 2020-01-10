package top.jfunc.common.http.component.apache;

import org.apache.http.client.methods.CloseableHttpResponse;
import top.jfunc.common.http.component.StreamExtractor;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.ApacheUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheResponseStreamExtractor implements StreamExtractor<CloseableHttpResponse> {
    @Override
    public InputStream extract(CloseableHttpResponse response, HttpRequest httpRequest) throws IOException {
        return ApacheUtil.getStreamFrom(response.getEntity(), httpRequest.isIgnoreResponseBody());
    }
}
