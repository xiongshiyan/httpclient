package top.jfunc.common.http.smart;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import top.jfunc.common.http.smart.RequestSender;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJoddSender implements RequestSender<HttpRequest , HttpResponse> {
    @Override
    public HttpResponse send(HttpRequest httpRequest) throws IOException{
        return httpRequest.send();
    }
}
