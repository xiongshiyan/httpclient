package top.jfunc.common.http.component.apache;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.AbstractBodyContentCallbackCreator;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.util.ApacheUtil;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheBodyContentCallbackCreator extends AbstractBodyContentCallbackCreator<HttpRequest> {
    @Override
    protected ContentCallback<HttpRequest> doCreate(StringBodyRequest stringBodyRequest) throws IOException {
        return request -> setRequestBody(request, stringBodyRequest.getBody(), stringBodyRequest.calculateBodyCharset());
    }

    protected void setRequestBody(HttpRequest request, String body, String bodyCharset){
        if(body == null || !(request instanceof HttpEntityEnclosingRequest)){return;}

        ApacheUtil.setRequestBody((HttpEntityEnclosingRequest)request, body, bodyCharset);
    }
}
