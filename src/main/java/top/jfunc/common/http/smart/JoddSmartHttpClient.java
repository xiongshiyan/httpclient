package top.jfunc.common.http.smart;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.JoddHttpClient;
import top.jfunc.common.utils.IoUtil;

import java.io.File;
import java.io.IOException;

/**
 * 使用Jodd-http 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class JoddSmartHttpClient extends JoddHttpClient implements SmartHttpClient , SmartHttpTemplate<HttpRequest> {

    @Override
    public JoddSmartHttpClient setConfig(Config config) {
        super.setConfig(config);
        return this;
    }

    @Override
    public <R> R template(Request request, Method method , ContentCallback<HttpRequest> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        //1.获取完成的URL，创建请求
        String completedUrl = addBaseUrlIfNecessary(request.getUrl());
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.method(method.name());
        httpRequest.set(completedUrl);

        //2.设置header
        setRequestHeaders(httpRequest , request.getContentType() , mergeDefaultHeaders(request.getHeaders()));

        //3.超时设置
        httpRequest.connectionTimeout(getConnectionTimeoutWithDefault(request.getConnectionTimeout()));
        httpRequest.timeout(getReadTimeoutWithDefault(request.getReadTimeout()));

        //4.SSL设置
        if(ParamUtil.isHttps(completedUrl)){
            //默认设置这些
            initSSL(httpRequest , RequestSSLUtil.getHostnameVerifier(request , getHostnameVerifier()) ,
                    RequestSSLUtil.getSSLSocketFactory(request , getSSLSocketFactory()) ,
                    RequestSSLUtil.getX509TrustManager(request , getX509TrustManager()), request.getProxyInfo());
        }

        //5.处理body
        if(contentCallback != null && method.hasContent()){
            contentCallback.doWriteWith(httpRequest);
        }

        //6.子类可以复写
        doWithHttpRequest(httpRequest);

        //7.真正请求
        HttpResponse response = httpRequest.send();

        //8.返回处理
        return resultCallback.convert(response.statusCode() ,
                getStreamFrom(response , request.isIgnoreResponseBody()),
                getResultCharsetWithDefault(request.getResultCharset()) ,
                parseHeaders(response , request.isIncludeHeaders()));
    }

    @Override
    public Response get(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(ParamUtil.contactUrlParams(request.getUrl(), request.getParams() , request.getBodyCharset()), Method.GET,
                request.getContentType(), null, request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(),
                request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request.setUrl(ParamUtil.contactUrlParams(request.getUrl(), request.getParams(), getBodyCharsetWithDefault(request.getBodyCharset()))) ,
                Method.GET , null , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response post(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(),
                r -> setRequestBody(r, request.getBodyIfNullWithParams(), request.getBodyCharset()), request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request, Method.POST, httpRequest -> httpRequest.body(request.getBodyIfNullWithParams().getBytes(getBodyCharsetWithDefault(request.getBodyCharset())) , request.getContentType()),
                Response::with);

        return afterTemplate(request , response);
    }

    @Override
    public Response httpMethod(Request req, Method method) throws IOException {
        Request request = beforeTemplate(req);
        ContentCallback<HttpRequest> contentCallback = null;
        if(method.hasContent()){
            contentCallback = httpRequest -> httpRequest.body(request.getBodyIfNullWithParams().getBytes(getBodyCharsetWithDefault(request.getBodyCharset())) , request.getContentType());
        }
        Response response = template(request, method , contentCallback, Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public byte[] getAsBytes(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(), Method.GET, request.getContentType(),null, request.getHeaders(),
                request.getConnectionTimeout(),request.getReadTimeout() , request.getResultCharset(),request.isIncludeHeaders(),
                (s,b,r,h)-> IoUtil.stream2Bytes(b));*/
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(), Method.GET, request.getContentType(),null, request.getHeaders() ,
                request.getConnectionTimeout(),request.getReadTimeout() , request.getResultCharset(),request.isIncludeHeaders(),
                (s,b,r,h)-> IoUtil.copy2File(b, request.getFile()));*/
        return template(request , Method.GET, null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(), r -> addFormFiles(r, request.getFormFiles()),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        /*使用更全的 ，支持文件和参数一起上传 */

        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(), r -> addFormFiles(r, request.getParams() , request.getFormFiles()),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request , Method.POST ,
                r -> upload0(r, request.getParams(), request.getFormFiles()) , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response afterTemplate(Request request, Response response) throws IOException{
        if(request.isRedirectable() && response.needRedirect()){
            return get(Request.of(response.getRedirectUrl()));
        }
        return response;
    }
}
