package top.jfunc.common.http.exe.okhttp3;

import okhttp3.Request;
import top.jfunc.common.http.component.okhttp3.DefaultOkHttp3BodyContentCallbackCreator;
import top.jfunc.common.http.component.okhttp3.DefaultOkHttp3UploadContentCallbackCreator;
import top.jfunc.common.http.exe.AbstractExeSmartHttpClient;

/**
 * 使用OkHttp3实现的Http请求类
 * @since 1.2.12
 * @since 2020.12.01
 * @author 熊诗言2020/12/01
 */
public class OkHttp3ExeSmartHttpClient extends AbstractExeSmartHttpClient<Request.Builder> {
    @Override
    protected void init() {
        super.init();

        setBodyContentCallbackCreator(new DefaultOkHttp3BodyContentCallbackCreator());
        setUploadContentCallbackCreator(new DefaultOkHttp3UploadContentCallbackCreator());

        setHttpRequestExecutor(new OkHttp3HttpRequestExecutor());
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by square's OkHttp3";
    }
}
