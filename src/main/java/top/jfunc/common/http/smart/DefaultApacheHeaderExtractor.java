package top.jfunc.common.http.smart;

import org.apache.http.HttpResponse;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.ApacheUtil;
import top.jfunc.common.utils.MultiValueMap;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheHeaderExtractor extends AbstractHeaderExtractor<HttpResponse> {
    @Override
    protected MultiValueMap<String, String> doExtractHeaders(HttpResponse httpResponse, HttpRequest httpRequest) {
        return ApacheUtil.parseHeaders(httpResponse , httpRequest.isIncludeHeaders());
    }
}
