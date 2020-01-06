package top.jfunc.common.http.smart;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.jfunc.common.http.smart.RequestExecutor;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3RequestExecutor implements RequestExecutor<OkHttpClient, Request.Builder, Response> {
    @Override
    public Response execute(OkHttpClient okHttpClient, Request.Builder builder) throws IOException {
        return okHttpClient.newCall(builder.build()).execute();
    }
}
