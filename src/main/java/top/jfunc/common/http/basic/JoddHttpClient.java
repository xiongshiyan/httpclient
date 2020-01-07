package top.jfunc.common.http.basic;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.util.JoddUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;

import static top.jfunc.common.http.util.JoddUtil.upload0;

/**
 * 使用Jodd-Http 实现的Http请求类
 * @author xiongshiyan at 2019/5/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class JoddHttpClient extends AbstractImplementHttpClient<HttpRequest> {

    @Override
    public <R> R doInternalTemplate(String url, Method method, String contentType, ContentCallback<HttpRequest> contentCallback, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset, boolean includeHeaders, ResultCallback<R> resultCallback) throws Exception {
        HttpResponse response = null;
        try {
            //1.获取完成的URL，创建请求
            String completedUrl = handleUrlIfNecessary(url);
            HttpRequest request = createAndConfigHttpRequest(method, completedUrl, connectTimeout, readTimeout);

            //4.处理body
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(request);
            }

            //5.设置header
            configHeaders(request , completedUrl , contentType , headers);

            //6.子类可以复写
            doWithHttpRequest(request);

            //7.真正请求
            response = request.send();

            //8.获取返回值
            InputStream inputStream = getStreamFrom(response, false);

            //9.返回header,包括Cookie处理
            MultiValueMap<String , String> responseHeaders = determineHeaders(response , completedUrl , includeHeaders);

            //8.返回处理
            return resultCallback.convert(response.statusCode() , inputStream,
                    getConfig().getResultCharsetWithDefault(resultCharset) ,
                    responseHeaders);
        } finally {
            if(null != response){
                response.close();
            }
        }
    }

    protected InputStream getStreamFrom(HttpResponse httpResponse , boolean ignoreResponseBody) throws IOException{
        return JoddUtil.getStreamFrom(httpResponse , ignoreResponseBody);
    }

    protected HttpRequest createAndConfigHttpRequest(Method method , String completedUrl , int connectionTimeout , int readTimeout){
        HttpRequest request = new HttpRequest();
        request.method(method.name());
        request.set(completedUrl);


        //2.超时设置
        Config config = getConfig();
        request.connectionTimeout(config.getConnectionTimeoutWithDefault(connectionTimeout));
        request.timeout(config.getReadTimeoutWithDefault(readTimeout));

        //3.SSL设置
        initSSL(request);

        return request;
    }

    protected void initSSL(HttpRequest request){
        JoddUtil.initSSL(request ,
                getDefaultHostnameVerifier() ,
                getDefaultSSLSocketFactory() ,
                getDefaultX509TrustManager(),
                getConfig().getDefaultProxyInfo());
    }


    @Override
    protected void setRequestHeaders(Object target, String contentType, MultiValueMap<String, String> handledHeaders) throws IOException {
        JoddUtil.setRequestHeaders((HttpRequest)target , contentType , handledHeaders);
    }

    /**
     * 留给子类做更多的配置
     * @param joddHttpRequest jodd的请求
     */
    protected void doWithHttpRequest(HttpRequest joddHttpRequest){}


    @Override
    protected MultiValueMap<String, String> parseResponseHeaders(Object source, boolean includeHeaders) {
        return JoddUtil.parseHeaders((HttpResponse)source , includeHeaders);
    }

    @Override
    protected ContentCallback<HttpRequest> bodyContentCallback(Method method , String body, String bodyCharset, String contentType) throws IOException {
        String type = null == contentType ?
                MediaType.APPLICATIPON_JSON.withCharset(bodyCharset).toString() : contentType;
        return httpRequest -> httpRequest.bodyText(body , type, bodyCharset);
    }

    @Override
    protected ContentCallback<HttpRequest> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        return httpRequest -> upload0(httpRequest , params , paramCharset , formFiles);
    }

    @Override
    public String toString() {
        return "HttpClient implemented by Jodd-Http";
    }
}
