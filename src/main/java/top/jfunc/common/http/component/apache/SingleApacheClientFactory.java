package top.jfunc.common.http.component.apache;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import top.jfunc.common.http.component.AbstractRequesterFactory;
import top.jfunc.common.http.component.CloseNoneCloser;
import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;

/**
 * 要配合{@link CloseNoneCloser}不能关闭这个client
 * @see DefaultApacheClientFactory
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SingleApacheClientFactory extends AbstractRequesterFactory<HttpClient> {
    private static final CloseableHttpClient CLOSEABLE_HTTP_CLIENT = HttpClients.createDefault();

    @Override
    public HttpClient doCreate(HttpRequest httpRequest) throws IOException {
        return CLOSEABLE_HTTP_CLIENT;
    }
}
