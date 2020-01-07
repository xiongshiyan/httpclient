package top.jfunc.common.http.smart;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

import static top.jfunc.common.http.util.ApacheUtil.createHttpUriRequest;
import static top.jfunc.common.http.util.ApacheUtil.setRequestProperty;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheRequestFactory implements RequesterFactory<HttpUriRequest> {

    @Override
    public HttpUriRequest create(HttpRequest httpRequest, Method method, String completedUrl) throws IOException{
        Config config = httpRequest.getConfig();
        HttpUriRequest httpUriRequest = createHttpUriRequest(completedUrl, method);

        //2.设置请求参数
        setRequestProperty((HttpRequestBase) httpUriRequest,
                config.getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()),
                config.getReadTimeoutWithDefault(httpRequest.getReadTimeout()),
                config.getProxyInfoWithDefault(httpRequest.getProxyInfo()));
        return httpUriRequest;
    }
}
