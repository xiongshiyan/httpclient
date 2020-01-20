package top.jfunc.common.http.component.okhttp3;

import okhttp3.Response;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.component.AbstractHeaderExtractor;
import top.jfunc.common.http.util.OkHttp3Util;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3HeaderExtractor extends AbstractHeaderExtractor<Response> {
    @Override
    protected MultiValueMap<String, String> doExtractHeaders(Response response, HttpRequest httpRequest) throws IOException {
        return OkHttp3Util.parseHeaders(response);
    }
}
