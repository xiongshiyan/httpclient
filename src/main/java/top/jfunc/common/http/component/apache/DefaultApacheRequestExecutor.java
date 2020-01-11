package top.jfunc.common.http.component.apache;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import top.jfunc.common.http.component.RequestExecutor;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheRequestExecutor implements RequestExecutor<HttpClient , HttpUriRequest, HttpResponse> {

    @Override
    public HttpResponse execute(HttpClient httpClient, HttpUriRequest uriRequest , HttpRequest httpRequest) throws IOException {
        return httpClient.execute(uriRequest  , HttpClientContext.create());
    }
}
