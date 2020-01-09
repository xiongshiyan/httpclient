package top.jfunc.common.http.smart.jodd;

import jodd.http.HttpResponse;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.AbstractHeaderExtractor;
import top.jfunc.common.http.util.JoddUtil;
import top.jfunc.common.utils.MultiValueMap;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJoddHeaderExtractor extends AbstractHeaderExtractor<HttpResponse> {
    @Override
    protected MultiValueMap<String, String> doExtractHeaders(HttpResponse httpResponse, HttpRequest httpRequest) {
        return JoddUtil.parseHeaders(httpResponse , httpRequest.isIncludeHeaders());
    }
}
