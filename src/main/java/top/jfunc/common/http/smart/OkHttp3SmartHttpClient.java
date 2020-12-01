package top.jfunc.common.http.smart;

import okhttp3.Request;
import top.jfunc.common.http.component.okhttp3.DefaultOkHttp3BodyContentCallbackCreator;
import top.jfunc.common.http.component.okhttp3.DefaultOkHttp3UploadContentCallbackCreator;
import top.jfunc.common.http.component.okhttp3.OkHttp3HttpRequestExecutor;

/**
 * 使用OkHttp3实现的Http请求类
 * @since 1.2.12
 * @since 2020.12.01
 * @author 熊诗言2020/12/01
 */
public class OkHttp3SmartHttpClient extends BaseExeSmartHttpClient<Request.Builder> {

    public OkHttp3SmartHttpClient(){
        super(new DefaultOkHttp3BodyContentCallbackCreator(),
                new DefaultOkHttp3UploadContentCallbackCreator(),
                new OkHttp3HttpRequestExecutor());
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by square's OkHttp3";
    }
}
