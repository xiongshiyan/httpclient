package top.jfunc.common.http.smart;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import top.jfunc.common.http.smart.RequestExecutor;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheRequestExecutor implements RequestExecutor<CloseableHttpClient , HttpUriRequest, CloseableHttpResponse> {
    @Override
    public CloseableHttpResponse execute(CloseableHttpClient closeableHttpClient, HttpUriRequest uriRequest) throws IOException {
        return closeableHttpClient.execute(uriRequest  , HttpClientContext.create());
    }
}
