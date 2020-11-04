package top.jfunc.common.http.component.apache;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import top.jfunc.common.http.util.ParamUtil;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.component.AbstractRequesterFactory;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.ApacheUtil;
import top.jfunc.common.utils.ObjectUtil;
import top.jfunc.common.utils.StrUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;

import static top.jfunc.common.utils.StrUtil.COLON;
import static top.jfunc.common.utils.StrUtil.SLASH;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheClientFactory extends AbstractRequesterFactory<HttpClient> {
    private int maxTotal = 200;
    private int maxPerRoute = 40;
    private int maxRoute = 100;
    private int timeOfValidateAfterInactivity = 1000;

    @Override
    public HttpClient doCreate(HttpRequest httpRequest) throws IOException {
        HttpClientBuilder clientBuilder = createClientBuilder();
        configClientBuilder(clientBuilder, httpRequest);
        return clientBuilder.build();
    }

    protected void configClientBuilder(HttpClientBuilder clientBuilder, HttpRequest httpRequest) {
        Config config = httpRequest.getConfig();

        String completedUrl = httpRequest.getCompletedUrl();

        String hostname = completedUrl.split(SLASH)[2];
        boolean isHttps = ParamUtil.isHttps(completedUrl);
        int port = isHttps ? 443 : 80;
        if (hostname.contains(COLON)) {
            String[] arr = hostname.split(COLON);
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }

        HttpClientConnectionManager connectionManager = createConnectionManager(hostname, port);
        clientBuilder.setConnectionManager(connectionManager).setRetryHandler(ApacheUtil::retryIf);

        if(isHttps){
            //https默认设置这些
            HostnameVerifier hostnameVerifier = ObjectUtil.defaultIfNull(httpRequest.getHostnameVerifier(), config.sslHolder().getHostnameVerifier());
            SSLContext sslContext = ObjectUtil.defaultIfNull(httpRequest.getSslContext(), config.sslHolder().getSslContext());
            ApacheUtil.initSSL(clientBuilder, hostnameVerifier, sslContext);
        }
        //是否重定向
        boolean followRedirects = ObjectUtil.defaultIfNull(httpRequest.followRedirects() , config.followRedirects());
        if(!followRedirects){
            clientBuilder.disableRedirectHandling();
        }
    }

    protected HttpClientBuilder createClientBuilder(){
        return HttpClients.custom();
    }

    protected HttpClientConnectionManager createConnectionManager(String hostname, int port){
        ConnectionSocketFactory csf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register(StrUtil.HTTP, csf)
                .register(StrUtil.HTTPS, sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        // 设置不活动的连接1000ms之后Validate
        cm.setValidateAfterInactivity(timeOfValidateAfterInactivity);

        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);
        return cm;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public void setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public int getMaxRoute() {
        return maxRoute;
    }

    public void setMaxRoute(int maxRoute) {
        this.maxRoute = maxRoute;
    }

    public int getTimeOfValidateAfterInactivity() {
        return timeOfValidateAfterInactivity;
    }

    public void setTimeOfValidateAfterInactivity(int timeOfValidateAfterInactivity) {
        this.timeOfValidateAfterInactivity = timeOfValidateAfterInactivity;
    }
}
