package top.jfunc.common.http.exe.jdk;

import top.jfunc.common.http.base.*;
import top.jfunc.common.http.component.jdk.DefaultJdkBodyContentCallbackCreator;
import top.jfunc.common.http.component.jdk.DefaultJdkUploadContentCallbackCreator;
import top.jfunc.common.http.exe.AbstractExeSmartHttpClient;
import top.jfunc.common.http.exe.ClientHttpResponse;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.ObjectUtil;

import java.net.HttpURLConnection;

/**
 * 使用URLConnection实现的Http请求类
 * @since 1.2.12
 * @since 2020.12.01
 * @author 熊诗言2020/12/01
 */
public class NativeExeSmartHttpClient extends AbstractExeSmartHttpClient<HttpURLConnection> {
    @Override
    protected void init() {
        super.init();

        setBodyContentCallbackCreator(new DefaultJdkBodyContentCallbackCreator());
        setUploadContentCallbackCreator(new DefaultJdkUploadContentCallbackCreator());

        setHttpRequestExecutor(new JdkHttpRequestExecutor());
    }

    @Override
    protected <R> R doInternalTemplate(HttpRequest httpRequest, ContentCallback<HttpURLConnection> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        ClientHttpResponse clientHttpResponse= null;
        try {
            getCookieAccessor().addCookieIfNecessary(httpRequest);
            clientHttpResponse = getHttpRequestExecutor().execute(httpRequest, contentCallback);
            MultiValueMap<String, String> responseHeaders = clientHttpResponse.getHeaders();
            getCookieAccessor().saveCookieIfNecessary(httpRequest, responseHeaders);

            //jdk对于重定向的特殊处理
            if(needRedirect(httpRequest, clientHttpResponse.getStatusCode(), responseHeaders)){
                String redirectUrl = responseHeaders.getFirst(HttpHeaders.LOCATION);
                HttpRequest hr = createRedirectHttpRequest(httpRequest, redirectUrl);
                return doInternalTemplate(hr, null, resultCallback);
            }

            return resultCallback.convert(clientHttpResponse.getStatusCode(), clientHttpResponse.getBody(), calculateResultCharset(httpRequest), responseHeaders);
        } finally {
            IoUtil.close(clientHttpResponse);
        }
    }
    protected HttpRequest createRedirectHttpRequest(HttpRequest httpRequest, String redirectUrl) {
        HttpRequest hr = getHttpRequestFactory().create(redirectUrl , null , null , httpRequest.getConnectionTimeout() , httpRequest.getReadTimeout() , httpRequest.getResultCharset());
        init(hr , Method.GET);
        //处理多次重定向的情况
        hr.followRedirects(Config.FOLLOW_REDIRECTS);
        return hr;
    }
    /**
     * 判断是否重定向
     */
    protected boolean needRedirect(HttpRequest httpRequest, int statusCode, MultiValueMap<String, String> responseHeaders) {
        Config config = httpRequest.getConfig();
        boolean followRedirects = ObjectUtil.defaultIfNull(httpRequest.followRedirects() , config.followRedirects());
        return followRedirects && HttpStatus.needRedirect(statusCode)
                && MapUtil.notEmpty(responseHeaders)
                && responseHeaders.containsKey(HttpHeaders.LOCATION);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by JDK's HttpURLConnection";
    }
}