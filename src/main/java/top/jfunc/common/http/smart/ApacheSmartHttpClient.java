package top.jfunc.common.http.smart;

import org.apache.http.HttpRequest;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.apache.DefaultApacheBodyContentCallbackCreator;
import top.jfunc.common.http.component.apache.DefaultApacheUploadContentCallbackCreator;
import top.jfunc.common.http.component.apache.ApacheHttpRequestExecutor;
import top.jfunc.common.http.cookie.CookieAccessor;

/**
 * 使用Apache HttpClient 实现的Http请求类
 * @author 熊诗言2020/12/01
 */
public class ApacheSmartHttpClient extends AbstractImplementSmartHttpClient<HttpRequest> {

    public ApacheSmartHttpClient(){
        super(new DefaultApacheBodyContentCallbackCreator(),
                new DefaultApacheUploadContentCallbackCreator(),
                new ApacheHttpRequestExecutor());
    }

    public ApacheSmartHttpClient(ContentCallbackCreator<HttpRequest> bodyContentCallbackCreator,
                                 ContentCallbackCreator<HttpRequest> uploadContentCallbackCreator,
                                 HttpRequestExecutor<HttpRequest> httpRequestExecutor) {
        super(bodyContentCallbackCreator, uploadContentCallbackCreator, httpRequestExecutor);
    }

    public ApacheSmartHttpClient(ContentCallbackCreator<HttpRequest> bodyContentCallbackCreator,
                                 ContentCallbackCreator<HttpRequest> uploadContentCallbackCreator,
                                 HttpRequestExecutor<HttpRequest> httpRequestExecutor,
                                 CookieAccessor cookieAccessor) {
        super(bodyContentCallbackCreator, uploadContentCallbackCreator, httpRequestExecutor, cookieAccessor);
    }

    public ApacheSmartHttpClient(AssemblingFactory assemblingFactory,
                                 ContentCallbackCreator<HttpRequest> bodyContentCallbackCreator,
                                 ContentCallbackCreator<HttpRequest> uploadContentCallbackCreator,
                                 HttpRequestExecutor<HttpRequest> httpRequestExecutor,
                                 CookieAccessor cookieAccessor) {
        super(assemblingFactory, bodyContentCallbackCreator, uploadContentCallbackCreator, httpRequestExecutor, cookieAccessor);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by Apache's httpcomponents";
    }
}
