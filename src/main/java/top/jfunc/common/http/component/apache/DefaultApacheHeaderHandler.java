package top.jfunc.common.http.component.apache;

import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.component.AbstractHeaderHandler;
import top.jfunc.common.http.util.ApacheUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheHeaderHandler extends AbstractHeaderHandler<org.apache.http.HttpRequest> {
    @Override
    protected void doConfigHeaders(org.apache.http.HttpRequest request, HttpRequest httpRequest, MultiValueMap<String, String> handledHeaders) throws IOException {
        ApacheUtil.setRequestHeaders(request , httpRequest.getContentType() , handledHeaders);
    }
}
