package top.jfunc.common.http.basic;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

import static top.jfunc.common.http.util.JoddUtil.*;

/**
 * 使用Jodd-Http 实现的Http请求类
 * @author xiongshiyan at 2019/5/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class JoddHttpClient extends AbstractHttpClient<HttpRequest> {

    @Override
    public <R> R doInternalTemplate(String url, Method method, String contentType, ContentCallback<HttpRequest> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, boolean includeHeaders, ResultCallback<R> resultCallback) throws Exception {
        HttpResponse response = null;
        try {
            //1.获取完成的URL，创建请求
            String completedUrl = addBaseUrlIfNecessary(url);
            HttpRequest request = new HttpRequest();
            request.method(method.name());
            request.set(completedUrl);

            //2.超时设置
            request.connectionTimeout(getConnectionTimeoutWithDefault(connectTimeout));
            request.timeout(getReadTimeoutWithDefault(readTimeout));


            //3.SSL处理
            initSSL(request , getHostnameVerifier() , getSSLSocketFactory() , getX509TrustManager() , null);

            //4.处理body
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(request);
            }

            //5.设置header
            setRequestHeaders(request , contentType , mergeDefaultHeaders(headers));

            //6.子类可以复写
            doWithHttpRequest(request);

            //7.真正请求
            response = request.send();

            //8.返回处理
            return resultCallback.convert(response.statusCode() ,
                    getStreamFrom(response , false),
                    getResultCharsetWithDefault(resultCharset) ,
                    parseHeaders(response , includeHeaders));
        } finally {
            if(null != response){
                response.close();
            }
        }
    }

    protected void doWithHttpRequest(HttpRequest httpRequest){}

    @Override
    protected ContentCallback<HttpRequest> bodyContentCallback(String body, String bodyCharset, String contentType) throws IOException {
        String type = null == contentType ?
                MediaType.APPLICATIPON_JSON.withCharset(bodyCharset).toString() : contentType;
        return httpRequest -> httpRequest.bodyText(body , type, bodyCharset);
    }

    @Override
    protected ContentCallback<HttpRequest> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, FormFile[] formFiles) throws IOException {
        return httpRequest -> upload0(httpRequest , params , paramCharset , formFiles);
    }

    @Override
    public String toString() {
        return "HttpClient implemented by Jodd-Http";
    }
}
