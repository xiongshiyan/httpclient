package top.jfunc.common.http.smart;

import org.apache.http.HttpRequest;
import top.jfunc.common.http.component.BodyContentCallbackCreator;
import top.jfunc.common.http.component.HttpRequestExecutor;
import top.jfunc.common.http.component.UploadContentCallbackCreator;
import top.jfunc.common.http.component.apache.DefaultApacheBodyContentCallbackCreator;
import top.jfunc.common.http.component.apache.DefaultApacheUploadContentCallbackCreator;
import top.jfunc.common.http.component.apache.ApacheHttpRequestExecutor;
import top.jfunc.common.http.component.httprequest.HttpRequestFactory;
import top.jfunc.common.http.component.httprequest.StringBodyHttpRequestFactory;
import top.jfunc.common.http.component.httprequest.UploadRequestFactory;
import top.jfunc.common.http.cookie.CookieAccessor;

/**
 * 使用Apache HttpClient 实现的Http请求类
 * @since 1.2.12
 * @since 2020.12.01
 * @author 熊诗言2020/12/01
 */
public class ApacheSmartHttpClient extends AbstractImplementSmartHttpClient<HttpRequest> {

    public ApacheSmartHttpClient(){
        super(new DefaultApacheBodyContentCallbackCreator(),
                new DefaultApacheUploadContentCallbackCreator(),
                new ApacheHttpRequestExecutor());
    }

    public ApacheSmartHttpClient(BodyContentCallbackCreator<HttpRequest> bodyContentCallbackCreator,
                                 UploadContentCallbackCreator<HttpRequest> uploadContentCallbackCreator,
                                 HttpRequestExecutor<HttpRequest> httpRequestExecutor) {
        super(bodyContentCallbackCreator,
                uploadContentCallbackCreator,
                httpRequestExecutor);
    }

    public ApacheSmartHttpClient(BodyContentCallbackCreator<HttpRequest> bodyContentCallbackCreator,
                                 UploadContentCallbackCreator<HttpRequest> uploadContentCallbackCreator,
                                 HttpRequestExecutor<HttpRequest> httpRequestExecutor,
                                 CookieAccessor cookieAccessor) {
        super(bodyContentCallbackCreator,
                uploadContentCallbackCreator,
                httpRequestExecutor,
                cookieAccessor);
    }

    public ApacheSmartHttpClient(HttpRequestFactory httpRequestFactory,
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
        return "SmartHttpClient implemented by Apache's httpcomponents";
    }
}
