package top.jfunc.common.http.component.okhttp3;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.jfunc.common.http.component.RequestExecutor;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3RequestExecutor implements RequestExecutor<OkHttpClient, Request, Response> {
    @Override
    public Response execute(OkHttpClient okHttpClient, Request request) throws IOException {
        return okHttpClient.newCall(request).execute();
    }
}
