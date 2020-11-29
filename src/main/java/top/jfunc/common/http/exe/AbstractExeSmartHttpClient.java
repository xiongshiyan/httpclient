package top.jfunc.common.http.exe;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.AbstractImplementSmartHttpClient;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

/**
 * 利用一个执行器{@link HttpRequestExecutor}来执行
 * @since 1.2.12
 * @since 2020.12.01
 * @author 熊诗言2020/12/01
 */
public class AbstractExeSmartHttpClient<CC> extends AbstractImplementSmartHttpClient<CC> {
    private HttpRequestExecutor<CC> httpRequestExecutor;

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest , ContentCallback<CC> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        ClientHttpResponse clientHttpResponse= null;
        try {
            getCookieAccessor().addCookieIfNecessary(httpRequest);

            clientHttpResponse = getHttpRequestExecutor().execute(httpRequest, contentCallback);
            MultiValueMap<String, String> responseHeaders = clientHttpResponse.getHeaders();

            getCookieAccessor().saveCookieIfNecessary(httpRequest, responseHeaders);

            return resultCallback.convert(clientHttpResponse.getStatusCode(), clientHttpResponse.getBody(), calculateResultCharset(httpRequest), responseHeaders);
        } finally {
            IoUtil.close(clientHttpResponse);
        }
    }

    public HttpRequestExecutor<CC> getHttpRequestExecutor() {
        return httpRequestExecutor;
    }

    public void setHttpRequestExecutor(HttpRequestExecutor<CC> httpRequestExecutor) {
        this.httpRequestExecutor = httpRequestExecutor;
    }
}
