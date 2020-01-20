package top.jfunc.common.http.component.apache;

import org.apache.http.HttpEntity;
import top.jfunc.common.http.component.AbstractStreamExtractor;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.ApacheUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheEntityStreamExtractor extends AbstractStreamExtractor<HttpEntity> {
    @Override
    public InputStream doExtract(HttpEntity httpEntity, HttpRequest httpRequest) throws IOException {
        return ApacheUtil.getStreamFrom(httpEntity);
    }
}
