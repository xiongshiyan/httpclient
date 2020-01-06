package top.jfunc.common.http.smart;

import org.apache.http.client.methods.HttpUriRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.ApacheUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheHeaderHandler extends AbstractHeaderHandler<HttpUriRequest> {
    @Override
    protected void doConfigHeaders(HttpUriRequest uriRequest, HttpRequest httpRequest, MultiValueMap<String, String> handledHeaders) throws IOException {
        ApacheUtil.setRequestHeaders(uriRequest , httpRequest.getContentType() , handledHeaders);
    }
}
