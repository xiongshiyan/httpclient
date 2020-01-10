package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.InputStream;
import java.net.Socket;

/**
 * @author xiongshiyan at 2019/5/9 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DemoImpl extends AbstractImplementSmartHttpClient<Socket> {
    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, Method method, ContentCallback<Socket> contentCallback, ResultCallback<R> resultCallback) throws Exception {

        //1.获取真实请求的URL
        String completedUrl = httpRequest.getCompletedUrl();
        //2.创建请求

        //3.设置header

        //4.设置body...

        Socket socket = null;
        getContentCallbackHandler().handle(socket , contentCallback , httpRequest , method);

        //5.获取执行器执行

        //6.获取响应的statusCode
        int statusCode = 200;

        //7.获取响应body
        InputStream inputStream = null;

        //8.获取响应header
        MultiValueMap<String , String> responseHeaders = null;

        return resultCallback.convert(200 , inputStream , getConfig().getResultCharsetWithDefault(httpRequest.getResultCharset()) , responseHeaders);
    }
}
