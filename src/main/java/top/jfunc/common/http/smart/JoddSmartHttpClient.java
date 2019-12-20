package top.jfunc.common.http.smart;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.util.JoddUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;

import static top.jfunc.common.http.util.JoddUtil.upload0;

/**
 * 使用Jodd-http 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class JoddSmartHttpClient extends AbstractImplementSmartHttpClient<HttpRequest> {

    @Override
    protected <R> R doInternalTemplate(top.jfunc.common.http.request.HttpRequest httpRequest, Method method , ContentCallback<HttpRequest> contentCallback , ResultCallback<R> resultCallback) throws Exception {
        HttpResponse response = null;
        try {
            //1.获取完成的URL，创建请求
            String completedUrl = handleUrlIfNecessary(httpRequest);
            HttpRequest request = createAndConfigHttpRequest(httpRequest, method, completedUrl);

            //4.处理body
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(request);
            }

            //5.设置header
            configHeaders(request , httpRequest , completedUrl);

            //6.子类可以复写
            doWithHttpRequest(request , httpRequest);

            //7.真正请求
            response = request.send();

            //8.获取返回值
            InputStream inputStream = getStreamFrom(response, httpRequest);

            //9.返回header,包括Cookie处理
            MultiValueMap<String , String> responseHeaders = determineHeaders(response , httpRequest , completedUrl);

            return resultCallback.convert(response.statusCode(), inputStream,
                    getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    responseHeaders);
        } finally {
            if(null != response){
                response.close();
            }
        }
    }

    protected InputStream getStreamFrom(HttpResponse httpResponse , top.jfunc.common.http.request.HttpRequest httpRequest) throws IOException{
        return JoddUtil.getStreamFrom(httpResponse , httpRequest.isIgnoreResponseBody());
    }

    protected HttpRequest createAndConfigHttpRequest(top.jfunc.common.http.request.HttpRequest httpRequest , Method method , String completedUrl){
        HttpRequest request = new HttpRequest();
        request.method(method.name());
        request.set(completedUrl);

        //2.超时设置
        request.connectionTimeout(getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()));
        request.timeout(getReadTimeoutWithDefault(httpRequest.getReadTimeout()));

        //是否重定向
        request.followRedirects(httpRequest.followRedirects());

        //3.SSL设置
        initSSL(request , httpRequest);

        return request;
    }

    protected void initSSL(HttpRequest request , top.jfunc.common.http.request.HttpRequest httpRequest){
        JoddUtil.initSSL(request , getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier()) ,
                getSSLSocketFactoryWithDefault(httpRequest.getSslSocketFactory()) ,
                getX509TrustManagerWithDefault(httpRequest.getX509TrustManager()),
                getProxyInfoWithDefault(httpRequest.getProxyInfo()));
    }


    @Override
    protected void setRequestHeaders(Object target, top.jfunc.common.http.request.HttpRequest httpRequest, MultiValueMap<String, String> handledHeaders) throws IOException {
        JoddUtil.setRequestHeaders((HttpRequest)target , httpRequest.getContentType() , handledHeaders);
    }

    /**
     * 留给子类做更多的配置
     * @param joddHttpRequest jodd的请求
     * @param httpRequest 请求参数
     */
    protected void doWithHttpRequest(HttpRequest joddHttpRequest , top.jfunc.common.http.request.HttpRequest httpRequest){}


    @Override
    protected MultiValueMap<String, String> parseResponseHeaders(Object source, top.jfunc.common.http.request.HttpRequest httpRequest) {
        return JoddUtil.parseHeaders((HttpResponse)source , httpRequest.isIncludeHeaders());
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
        return "SmartHttpClient implemented by Jodd-Http";
    }
}
