package top.jfunc.common.http.smart;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.HttpRequestExecutor;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.response.ClientHttpResponse;

import java.io.IOException;

/**
 * 利用一个执行器{@link HttpRequestExecutor}来执行
 * @since 1.2.12
 * @since 2020.12.01
 * @author 熊诗言2020/12/01
 */
public abstract class BaseExeSmartHttpClient<CC> extends AbstractImplementSmartHttpClient<CC> {
    private HttpRequestExecutor<CC> httpRequestExecutor;

    @Override
    protected ClientHttpResponse doInternalTemplate(HttpRequest httpRequest , ContentCallback<CC> contentCallback) throws Exception {
        return execute(httpRequest, contentCallback);
    }

    protected ClientHttpResponse execute(HttpRequest httpRequest , ContentCallback<CC> contentCallback) throws IOException {
        getCookieAccessor().addCookieIfNecessary(httpRequest);

        ClientHttpResponse clientHttpResponse = getHttpRequestExecutor().execute(httpRequest, contentCallback);

        getCookieAccessor().saveCookieIfNecessary(httpRequest, clientHttpResponse.getHeaders());

        return clientHttpResponse;
    }

    public HttpRequestExecutor<CC> getHttpRequestExecutor() {
        return httpRequestExecutor;
    }

    public void setHttpRequestExecutor(HttpRequestExecutor<CC> httpRequestExecutor) {
        this.httpRequestExecutor = httpRequestExecutor;
    }
}
