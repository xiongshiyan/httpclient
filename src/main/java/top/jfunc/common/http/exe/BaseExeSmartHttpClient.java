package top.jfunc.common.http.exe;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.AbstractImplementSmartHttpClient;
import top.jfunc.common.utils.IoUtil;

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
    protected <R> R doInternalTemplate(HttpRequest httpRequest , ContentCallback<CC> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        ClientHttpResponse clientHttpResponse= null;
        try {
            clientHttpResponse = exe(httpRequest, contentCallback);

            return resultCallback.convert(clientHttpResponse.getStatusCode(), clientHttpResponse.getStatusText(), clientHttpResponse.getBody(), calculateResultCharset(httpRequest), clientHttpResponse.getHeaders());
        } finally {
            IoUtil.close(clientHttpResponse);
        }
    }

    protected ClientHttpResponse exe(HttpRequest httpRequest , ContentCallback<CC> contentCallback) throws IOException {
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
