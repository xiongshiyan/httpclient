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
public class DefaultApacheRequestExecutor implements RequestExecutor<HttpClient , org.apache.http.HttpRequest, HttpResponse> {

    @Override
    public HttpResponse execute(HttpClient httpClient, org.apache.http.HttpRequest request , HttpRequest httpRequest) throws IOException {
        return httpClient.execute((HttpUriRequest) request  , HttpClientContext.create());
    }
}
