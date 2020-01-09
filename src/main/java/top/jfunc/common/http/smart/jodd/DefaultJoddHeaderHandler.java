package top.jfunc.common.http.smart.jodd;

import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.AbstractHeaderHandler;
import top.jfunc.common.http.util.JoddUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJoddHeaderHandler extends AbstractHeaderHandler<jodd.http.HttpRequest> {
    @Override
    protected void doConfigHeaders(jodd.http.HttpRequest request, HttpRequest httpRequest, MultiValueMap<String, String> handledHeaders) throws IOException {
        JoddUtil.setRequestHeaders(request , httpRequest.getContentType() , handledHeaders);
    }
}
