package top.jfunc.common.http.smart;

import okhttp3.Request;
import top.jfunc.common.http.component.BodyContentCallbackCreator;
import top.jfunc.common.http.component.HttpRequestExecutor;
import top.jfunc.common.http.component.UploadContentCallbackCreator;
import top.jfunc.common.http.component.AssemblingFactory;
import top.jfunc.common.http.component.okhttp3.DefaultOkHttp3BodyContentCallbackCreator;
import top.jfunc.common.http.component.okhttp3.DefaultOkHttp3UploadContentCallbackCreator;
import top.jfunc.common.http.component.okhttp3.OkHttp3HttpRequestExecutor;
import top.jfunc.common.http.cookie.CookieAccessor;

/**
 * 使用OkHttp3实现的Http请求类
 * @author 熊诗言2020/12/01
 */
public class OkHttp3SmartHttpClient extends AbstractImplementSmartHttpClient<Request.Builder> {

    public OkHttp3SmartHttpClient(){
        super(new DefaultOkHttp3BodyContentCallbackCreator(),
                new DefaultOkHttp3UploadContentCallbackCreator(),
                new OkHttp3HttpRequestExecutor());
    }

    public OkHttp3SmartHttpClient(BodyContentCallbackCreator<Request.Builder> bodyContentCallbackCreator, UploadContentCallbackCreator<Request.Builder> uploadContentCallbackCreator, HttpRequestExecutor<Request.Builder> httpRequestExecutor) {
        super(bodyContentCallbackCreator, uploadContentCallbackCreator, httpRequestExecutor);
    }

    public OkHttp3SmartHttpClient(BodyContentCallbackCreator<Request.Builder> bodyContentCallbackCreator, UploadContentCallbackCreator<Request.Builder> uploadContentCallbackCreator, HttpRequestExecutor<Request.Builder> httpRequestExecutor, CookieAccessor cookieAccessor) {
        super(bodyContentCallbackCreator, uploadContentCallbackCreator, httpRequestExecutor, cookieAccessor);
    }

    public OkHttp3SmartHttpClient(AssemblingFactory assemblingFactory,
                                  BodyContentCallbackCreator<Request.Builder> bodyContentCallbackCreator,
                                  UploadContentCallbackCreator<Request.Builder> uploadContentCallbackCreator,
                                  HttpRequestExecutor<Request.Builder> httpRequestExecutor,
                                  CookieAccessor cookieAccessor) {
        super(assemblingFactory, bodyContentCallbackCreator, uploadContentCallbackCreator, httpRequestExecutor, cookieAccessor);
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by square's OkHttp3";
    }
}
