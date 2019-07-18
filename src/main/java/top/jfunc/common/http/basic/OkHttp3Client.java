package top.jfunc.common.http.basic;

import okhttp3.*;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static top.jfunc.common.http.util.OkHttp3Util.*;

/**
 * 使用OkHttp3实现的Http请求类
 * @author xiongshiyan at 2018/1/11
 */
public class OkHttp3Client extends AbstractHttpClient<Request.Builder> implements HttpTemplate<Request.Builder>, HttpClient {

    @Override
    public  <R> R doInternalTemplate(String url, Method method , String contentType , ContentCallback<Request.Builder> contentCallback , MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , boolean includeHeaders , ResultCallback<R> resultCallback) throws Exception{
        Objects.requireNonNull(url);
        Response response = null;
        InputStream inputStream = null;
        try {
            String completedUrl = addBaseUrlIfNecessary(url);
            //1.构造OkHttpClient
            OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                    .connectTimeout(getConnectionTimeoutWithDefault(connectTimeout), TimeUnit.MILLISECONDS)
                    .readTimeout(getReadTimeoutWithDefault(readTimeout), TimeUnit.MILLISECONDS);

            ////////////////////////////////////ssl处理///////////////////////////////////
            if(ParamUtil.isHttps(completedUrl)){
                //默认设置这些
                initSSL(clientBuilder , getHostnameVerifier() , getSSLSocketFactory() , getX509TrustManager());
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            //给子类复写的机会
            doWithBuilder(clientBuilder , ParamUtil.isHttps(completedUrl));

            OkHttpClient client = clientBuilder.build();

            doWithClient(client);

            //2.1设置URL
            Request.Builder builder = new Request.Builder().url(completedUrl);

            //2.2处理请求体
            if(null != contentCallback && method.hasContent()){
                contentCallback.doWriteWith(builder);
            }

            //2.3设置headers
            setRequestHeaders(builder , contentType , mergeDefaultHeaders(headers) , null);

            //3.构造请求
            Request request = builder.build();

            //4.执行请求
            response = client.newCall(request).execute();

            //5.获取响应
            inputStream = getStreamFrom(response , false);

            return resultCallback.convert(response.code() , inputStream, getResultCharsetWithDefault(resultCharset), parseHeaders(response , includeHeaders));
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(response);
        }
    }

    protected void doWithBuilder(OkHttpClient.Builder builder , boolean isHttps) throws Exception{
        //default do nothing, give children a chance to do more config
    }
    protected void doWithClient(OkHttpClient okHttpClient) throws Exception{
        //default do nothing, give children a chance to do more config
    }


    @Override
    protected ContentCallback<Request.Builder> bodyContentCallback(String body, String bodyCharset, String contentType) throws IOException {
        RequestBody stringBody = stringBody(body, bodyCharset, contentType);
        return d -> setRequestBody(d, Method.POST, stringBody);
    }

    @Override
    protected ContentCallback<Request.Builder> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, FormFile[] formFiles) throws IOException {
        MultipartBody filesBody = filesBody(null , formFiles);
        return d -> setRequestBody(d, Method.POST , filesBody);
    }
    @Override
    public String toString() {
        return "HttpClient implemented by square's OkHttp3";
    }
}
