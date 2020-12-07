package top.jfunc.common.http.smart;

import jodd.http.HttpRequest;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.jodd.DefaultJoddBodyContentCallbackCreator;
import top.jfunc.common.http.component.jodd.DefaultJoddUploadContentCallbackCreator;
import top.jfunc.common.http.component.jodd.JoddHttpRequestExecutor;
import top.jfunc.common.http.cookie.CookieAccessor;

/**
 * 使用Jodd实现的Http请求类
 * @author 熊诗言2020/12/01
 */
public class JoddSmartHttpClient extends AbstractImplementSmartHttpClient<HttpRequest> {

    public JoddSmartHttpClient() {
        super(new DefaultJoddBodyContentCallbackCreator(),
                new DefaultJoddUploadContentCallbackCreator(),
                new JoddHttpRequestExecutor());
    }

    public JoddSmartHttpClient(ContentCallbackCreator<HttpRequest> bodyContentCallbackCreator,
                               ContentCallbackCreator<HttpRequest> uploadContentCallbackCreator,
                               HttpRequestExecutor<HttpRequest> httpRequestExecutor) {
        super(bodyContentCallbackCreator, uploadContentCallbackCreator, httpRequestExecutor);
    }

    public JoddSmartHttpClient(ContentCallbackCreator<HttpRequest> bodyContentCallbackCreator,
                               ContentCallbackCreator<HttpRequest> uploadContentCallbackCreator,
                               HttpRequestExecutor<HttpRequest> httpRequestExecutor,
                               CookieAccessor cookieAccessor) {
        super(bodyContentCallbackCreator, uploadContentCallbackCreator, httpRequestExecutor, cookieAccessor);
    }

    public JoddSmartHttpClient(AssemblingFactory assemblingFactory,
                               ContentCallbackCreator<HttpRequest> bodyContentCallbackCreator,
                               ContentCallbackCreator<HttpRequest> uploadContentCallbackCreator,
                               HttpRequestExecutor<HttpRequest> httpRequestExecutor,
                               CookieAccessor cookieAccessor) {
        super(assemblingFactory, bodyContentCallbackCreator, uploadContentCallbackCreator, httpRequestExecutor, cookieAccessor);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by Jodd-Http";
    }
}
