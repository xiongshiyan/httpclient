package top.jfunc.common.http.component;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * 提供统一处理默认header的方法
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractHeaderHandler<C> implements HeaderHandler<C> {
    @Override
    public void configHeaders(C target, HttpRequest httpRequest) throws IOException {
        Config config = httpRequest.getConfig();
        //1.合并默认headers
        MultiValueMap<String, String> handledHeaders = config.mergeDefaultHeaders(httpRequest.getHeaders());

        //2.真正设置
        doConfigHeaders(target , httpRequest , handledHeaders);
    }
    /**
     * 真正的设置header
     * @param c C
     * @param httpRequest HttpRequest
     * @param handledHeaders 处理过后的headers
     * @throws IOException IOException
     */
    protected abstract void doConfigHeaders(C c, HttpRequest httpRequest , MultiValueMap<String , String> handledHeaders) throws IOException;
}
