package top.jfunc.common.http.component.apache;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import top.jfunc.common.http.component.AbstractStreamExtractor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheResponseStreamExtractor extends AbstractStreamExtractor<HttpResponse> {
    @Override
    public InputStream doExtract(HttpResponse response, HttpRequest httpRequest) throws IOException {
        HttpEntity entity = response.getEntity();
        return null == entity ? null : entity.getContent();
    }
}
