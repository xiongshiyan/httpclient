package top.jfunc.common.http.smart;

import jodd.http.HttpRequest;
import top.jfunc.common.http.component.BodyContentCallbackCreator;
import top.jfunc.common.http.component.HttpRequestExecutor;
import top.jfunc.common.http.component.UploadContentCallbackCreator;
import top.jfunc.common.http.component.httprequest.HttpRequestFactory;
import top.jfunc.common.http.component.httprequest.StringBodyHttpRequestFactory;
import top.jfunc.common.http.component.httprequest.UploadRequestFactory;
import top.jfunc.common.http.component.jodd.DefaultJoddBodyContentCallbackCreator;
import top.jfunc.common.http.component.jodd.DefaultJoddUploadContentCallbackCreator;
import top.jfunc.common.http.component.jodd.JoddHttpRequestExecutor;
import top.jfunc.common.http.cookie.CookieAccessor;

/**
 * 使用Jodd实现的Http请求类
 * @since 1.2.12
 * @since 2020.12.01
 * @author 熊诗言2020/12/01
 */
public class JoddSmartHttpClient extends AbstractImplementSmartHttpClient<HttpRequest> {

    public JoddSmartHttpClient() {
        super(new DefaultJoddBodyContentCallbackCreator(),
                new DefaultJoddUploadContentCallbackCreator(),
                new JoddHttpRequestExecutor());
    }

    public JoddSmartHttpClient(BodyContentCallbackCreator<HttpRequest> bodyContentCallbackCreator,
                               UploadContentCallbackCreator<HttpRequest> uploadContentCallbackCreator,
                               HttpRequestExecutor<HttpRequest> httpRequestExecutor) {
        super(bodyContentCallbackCreator,
                uploadContentCallbackCreator,
                httpRequestExecutor);
    }

    public JoddSmartHttpClient(BodyContentCallbackCreator<HttpRequest> bodyContentCallbackCreator,
                               UploadContentCallbackCreator<HttpRequest> uploadContentCallbackCreator,
                               HttpRequestExecutor<HttpRequest> httpRequestExecutor,
                               CookieAccessor cookieAccessor) {
        super(bodyContentCallbackCreator,
                uploadContentCallbackCreator,
                httpRequestExecutor,
                cookieAccessor);
    }

    public JoddSmartHttpClient(HttpRequestFactory httpRequestFactory,
                               StringBodyHttpRequestFactory stringBodyHttpRequestFactory,
                               UploadRequestFactory uploadRequestFactory,
                               BodyContentCallbackCreator<HttpRequest> bodyContentCallbackCreator,
                               UploadContentCallbackCreator<HttpRequest> uploadContentCallbackCreator,
                               HttpRequestExecutor<HttpRequest> httpRequestExecutor,
                               CookieAccessor cookieAccessor) {
        super(httpRequestFactory,
                stringBodyHttpRequestFactory,
                uploadRequestFactory,
                bodyContentCallbackCreator,
                uploadContentCallbackCreator,
                httpRequestExecutor,
                cookieAccessor);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by Jodd-Http";
    }
}
