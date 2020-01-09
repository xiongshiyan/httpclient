package top.jfunc.common.http.smart.jdk;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.HeaderHandler;
import top.jfunc.common.http.util.NativeUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJdkHeaderHandler implements HeaderHandler<HttpURLConnection> {
    @Override
    public void configHeaders(HttpURLConnection target, HttpRequest httpRequest, String completedUrl) throws IOException {
        Config config = httpRequest.getConfig();

        //1.合并默认headers
        MultiValueMap<String, String> handledHeaders = config.mergeDefaultHeaders(httpRequest.getHeaders());

        //2.处理cookie
        //requestHeaders = addCookieIfNecessary(completedUrl, requestHeaders);

        //3.真正设置
        doConfigHeaders(target , httpRequest , handledHeaders);
    }

    protected void doConfigHeaders(HttpURLConnection connection, HttpRequest httpRequest , MultiValueMap<String , String> handledHeaders) throws IOException{
        NativeUtil.setRequestHeaders(connection , httpRequest.getContentType() , handledHeaders);
    }
}
