package top.jfunc.common.http.component.okhttp3;

import okhttp3.Response;
import okhttp3.ResponseBody;
import top.jfunc.common.http.component.AbstractStreamExtractor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3StreamExtractor extends AbstractStreamExtractor<Response> {
    @Override
    protected InputStream doExtract(Response response, HttpRequest httpRequest) throws IOException {
        ResponseBody body = response.body();
        return null == body ? null : body.byteStream();
    }
}
