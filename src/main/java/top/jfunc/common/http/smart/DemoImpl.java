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
public class DemoImpl extends AbstractImplementSmartHttpClient<Socket> {

    @Override
    protected ContentCallback<Socket> bodyContentCallback(Method method , String body, String bodyCharset, String contentType) throws IOException {
        //(cc)->cc.getOutputStream().write(body.getBytes(bodyCharset));
        return null;
    }

    @Override
    protected ContentCallback<Socket> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset , Iterable<FormFile> formFiles) throws IOException {
        return null;
    }

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, Method method, ContentCallback<Socket> contentCallback, ResultCallback<R> resultCallback) throws Exception {
        return null;
    }
}
