package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.net.Socket;

/**
 * @author xiongshiyan at 2019/5/9 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DemoImpl extends AbstractSmartHttpClient<Socket> {

    @Override
    protected ContentCallback<Socket> bodyContentCallback(String body, String bodyCharset, String contentType) throws IOException {
        //(cc)->cc.getOutputStream().write(body.getBytes(bodyCharset));
        return null;
    }

    @Override
    protected ContentCallback<Socket> uploadContentCallback(MultiValueMap<String, String> params, FormFile[] formFiles) throws IOException {
        return null;
    }

    @Override
    public <R> R template(String url, Method method, String contentType, ContentCallback<Socket> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, boolean includeHeaders, ResultCallback<R> resultCallback) throws IOException {
        //实现具体Http请求
        return null;
    }

    @Override
    public <R> R template(HttpRequest request, Method method, ContentCallback<Socket> contentCallback, ResultCallback<R> resultCallback) throws IOException {
        //实现具体Http请求
        return null;
    }
}