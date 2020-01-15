package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.AbstractConfigurableHttp;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.request.FormRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.basic.GetRequest;
import top.jfunc.common.http.request.basic.PostBodyRequest;
import top.jfunc.common.http.request.basic.UpLoadRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 提供了{@link SmartHttpClient}接口的实现
 * @see SmartHttpClient
 * @author xiongshiyan at 2019/5/8 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractSmartHttpClient<CC> extends AbstractConfigurableHttp implements SmartHttpClient, SmartHttpTemplate<CC> {

    /**处理一般包含body的*/
    private BodyContentCallbackCreator<CC> bodyContentCallbackCreator;
    /**处理文件上传*/
    private UploadContentCallbackCreator<CC> uploadContentCallbackCreator;
    /**ContentCallback处理器*/
    private ContentCallbackHandler<CC> contentCallbackHandler;
    /**InputStream关闭器*/
    private Closer inputStreamCloser;

    public AbstractSmartHttpClient(){
        setContentCallbackHandler(new DefaultContentCallbackHandler<>());
        setInputStreamCloser(new DefaultCloser());
    }


    @Override
    public <R> R get(HttpRequest httpRequest , ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest, Method.GET);
        return template(httpRequest , null , resultCallback);
    }

    @Override
    public <R> R post(StringBodyRequest stringBodyRequest , ResultCallback<R> resultCallback) throws IOException {
        init(stringBodyRequest, Method.POST);
        ContentCallback<CC> contentCallback = getBodyContentCallbackCreator().create(stringBodyRequest);
        return template(stringBodyRequest , contentCallback , resultCallback);
    }

    /**
     * post就支持form表单
     * @see StringBodyRequest
     * @see FormRequest
     */
    @Override
    public <R> R form(FormRequest formRequest, ResultCallback<R> resultCallback) throws IOException{
        init(formRequest, Method.POST);
        return post(formRequest , resultCallback);
    }

    @Override
    public <R> R upload(UploadRequest uploadRequest , ResultCallback<R> resultCallback) throws IOException {
        init(uploadRequest, Method.POST);
        ContentCallback<CC> contentCallback = getUploadContentCallbackCreator().create(uploadRequest);
        return template(uploadRequest, contentCallback , resultCallback);
    }

    /*------------------------HTTP方法通用支持---------------------------*/

    @Override
    public <R> R http(HttpRequest httpRequest, Method method, ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest , method);
        ContentCallback<CC> contentCallback = null;
        if(method.hasContent() && httpRequest instanceof StringBodyRequest){
            contentCallback = getBodyContentCallbackCreator().create(httpRequest);
        }
        return template(httpRequest , contentCallback, resultCallback);
    }



    /*------------------------HEAD---------------------------*/

    @Override
    public <R> R head(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest , Method.HEAD);
        //必须要响应头
        httpRequest.setIncludeHeaders(HttpRequest.INCLUDE_HEADERS);
        //设置忽略响应体
        httpRequest.setIgnoreResponseBody(HttpRequest.IGNORE_RESPONSE_BODY);
        return template(httpRequest , null , resultCallback);
    }



    /*------------------------OPTIONS---------------------------*/

    @Override
    public <R> R options(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest , Method.OPTIONS);
        //必须要响应头
        httpRequest.setIncludeHeaders(HttpRequest.INCLUDE_HEADERS);
        //设置忽略响应体
        httpRequest.setIgnoreResponseBody(HttpRequest.IGNORE_RESPONSE_BODY);
        return template(httpRequest , null , resultCallback);
    }



    /*------------------------PUT---------------------------*/

    @Override
    public <R> R put(StringBodyRequest stringBodyRequest, ResultCallback<R> resultCallback) throws IOException {
        init(stringBodyRequest , Method.PUT);
        ContentCallback<CC> contentCallback = getBodyContentCallbackCreator().create(stringBodyRequest);
        return template(stringBodyRequest , contentCallback , resultCallback);
    }



    /*------------------------PATCH---------------------------*/

    @Override
    public <R> R patch(StringBodyRequest stringBodyRequest, ResultCallback<R> resultCallback) throws IOException {
        init(stringBodyRequest , Method.PATCH);
        ContentCallback<CC> contentCallback = getBodyContentCallbackCreator().create(stringBodyRequest);
        return template(stringBodyRequest ,contentCallback , resultCallback);
    }



    /*------------------------DELETE---------------------------*/

    @Override
    public <R> R delete(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest , Method.DELETE);
        return template(httpRequest , null , resultCallback);
    }



    /*------------------------TRACE---------------------------*/

    @Override
    public <R> R trace(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest , Method.TRACE);
        //必须要响应头
        httpRequest.setIncludeHeaders(HttpRequest.INCLUDE_HEADERS);
        //设置忽略响应体
        httpRequest.setIgnoreResponseBody(HttpRequest.IGNORE_RESPONSE_BODY);

        /// trace没有body
        /*
        ContentCallback<CC> contentCallback = null;
        if(httpRequest instanceof StringBodyRequest){
            StringBodyRequest stringBodyRequest = (StringBodyRequest) httpRequest;
            String bodyCharset = getConfig().calculateBodyCharset(stringBodyRequest.getBodyCharset() , stringBodyRequest.getContentType());
            String body = stringBodyRequest.getBody();
            contentCallback = bodyContentCallback(Method.TRACE ,body, bodyCharset, httpRequest.getContentType());
        }*/
        return template(httpRequest , null, resultCallback);
    }

    protected void init(HttpRequest httpRequest , Method method){
        if(null == httpRequest.getConfig()){
            httpRequest.setConfig(getConfig());
        }

        if(null == httpRequest.getMethod()){
            httpRequest.setMethod(method);
        }
    }


    /////////////////////////////////////////////////////UnpackedParameterHttpClient///////////////////////////////////////////////////////////////

    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout, String resultCharset) throws IOException{
        GetRequest getRequest = GetRequest.of(url);
        getRequest.setQueryParams(params).setHeaders(headers).setConnectionTimeout(connectTimeout).setReadTimeout(readTimeout);
        if(null != resultCharset){
            getRequest.setResultCharset(resultCharset);
        }
        return get(getRequest , (statusCode, inputStream, rc, h)-> IoUtil.read(inputStream ,rc));
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) throws IOException {
        PostBodyRequest postBodyRequest = PostBodyRequest.of(url);
        postBodyRequest.setBody(body , contentType).setBodyCharset(bodyCharset).setHeaders(headers).setConnectionTimeout(connectTimeout).setReadTimeout(readTimeout);
        if(null != resultCharset){
            postBodyRequest.setResultCharset(resultCharset);
        }
        return post(postBodyRequest , (statusCode, inputStream, rc, h)-> IoUtil.read(inputStream ,rc));
    }

    @Override
    public byte[] getAsBytes(String url, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout) throws IOException {
        GetRequest getRequest = GetRequest.of(url);
        getRequest.setHeaders(headers).setConnectionTimeout(connectTimeout).setReadTimeout(readTimeout);
        return get(getRequest , (statusCode, inputStream, rc, h)-> IoUtil.stream2Bytes(inputStream));
    }

    @Override
    public File getAsFile(String url, MultiValueMap<String, String> headers, File file, int connectTimeout, int readTimeout) throws IOException {
        GetRequest getRequest = GetRequest.of(url);
        getRequest.setHeaders(headers).setConnectionTimeout(connectTimeout).setReadTimeout(readTimeout);
        return get(getRequest , (statusCode, inputStream, rc, h)-> IoUtil.copy2File(inputStream, file));
    }


    @Override
    public String upload(String url, MultiValueMap<String,String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) throws IOException{
        UpLoadRequest upLoadRequest = UpLoadRequest.of(url);
        upLoadRequest.addFormFile(files).setHeaders(headers).setConnectionTimeout(connectTimeout).setReadTimeout(readTimeout);
        if(null != resultCharset){
            upLoadRequest.setResultCharset(resultCharset);
        }
        return upload(upLoadRequest , (statusCode, inputStream, rc, h)-> IoUtil.read(inputStream ,rc));
    }

    @Override
    public String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) throws IOException {
        UpLoadRequest upLoadRequest = UpLoadRequest.of(url);
        upLoadRequest.addFormFile(files).setHeaders(headers).setConnectionTimeout(connectTimeout).setReadTimeout(readTimeout);
        if(null != resultCharset){
            upLoadRequest.setResultCharset(resultCharset);
        }
        if(MapUtil.notEmpty(params)){
            upLoadRequest.setFormParams(params);
        }
        return upload(upLoadRequest , (statusCode, inputStream, rc, h)-> IoUtil.read(inputStream ,rc));
    }






    public BodyContentCallbackCreator<CC> getBodyContentCallbackCreator() {
        return bodyContentCallbackCreator;
    }

    public void setBodyContentCallbackCreator(BodyContentCallbackCreator<CC> bodyContentCallbackCreator) {
        this.bodyContentCallbackCreator = Objects.requireNonNull(bodyContentCallbackCreator);
    }

    public UploadContentCallbackCreator<CC> getUploadContentCallbackCreator() {
        return uploadContentCallbackCreator;
    }

    public void setUploadContentCallbackCreator(UploadContentCallbackCreator<CC> uploadContentCallbackCreator) {
        this.uploadContentCallbackCreator = Objects.requireNonNull(uploadContentCallbackCreator);
    }

    public ContentCallbackHandler<CC> getContentCallbackHandler() {
        return contentCallbackHandler;
    }

    public void setContentCallbackHandler(ContentCallbackHandler<CC> contentCallbackHandler) {
        this.contentCallbackHandler = Objects.requireNonNull(contentCallbackHandler);
    }

    public Closer getInputStreamCloser() {
        return inputStreamCloser;
    }

    public void setInputStreamCloser(Closer inputStreamCloser) {
        this.inputStreamCloser = Objects.requireNonNull(inputStreamCloser);
    }
}

