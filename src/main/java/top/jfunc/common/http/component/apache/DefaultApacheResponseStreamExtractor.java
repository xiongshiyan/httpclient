package top.jfunc.common.http.component.apache;

import org.apache.http.HttpResponse;
import top.jfunc.common.http.component.AbstractStreamExtractor;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.ApacheUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheResponseStreamExtractor extends AbstractStreamExtractor<HttpResponse> {
    @Override
    public InputStream doExtract(HttpResponse response, HttpRequest httpRequest) throws IOException {
        return ApacheUtil.getStreamFrom(response.getEntity());
    }
}
