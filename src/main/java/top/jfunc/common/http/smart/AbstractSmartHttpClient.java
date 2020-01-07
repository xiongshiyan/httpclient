package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.AbstractHttpClient;
import top.jfunc.common.http.request.FormRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.util.Objects;

/**
 * 提供了{@link SmartHttpClient}接口的实现
 * @see SmartHttpClient
 * @author xiongshiyan at 2019/5/8 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractSmartHttpClient<CC> extends AbstractHttpClient<CC> implements SmartHttpClient, SmartHttpTemplate<CC> {

    /**处理一般包含body的*/
    private BodyContentCallbackCreator<CC> bodyContentCallbackCreator;
    /**处理文件上传*/
    private UploadContentCallbackCreator<CC> uploadContentCallbackCreator;
    /**ContentCallback处理器*/
    private ContentCallbackHandler<CC> contentCallbackHandler;
    /**处理URL的*/
    private CompletedUrlCreator completedUrlCreator;

    public AbstractSmartHttpClient(){
        setContentCallbackHandler(new DefaultContentCallbackHandler<>());
        setCompletedUrlCreator(new DefaultCompletedUrlCreator());
    }


    @Override
    public <R> R get(HttpRequest httpRequest , ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest);
        return template(httpRequest, Method.GET , null , resultCallback);
    }

    @Override
    public <R> R post(StringBodyRequest stringBodyRequest , ResultCallback<R> resultCallback) throws IOException {
        init(stringBodyRequest);
        ContentCallback<CC> contentCallback = getBodyContentCallbackCreator().create(stringBodyRequest, Method.POST);
        return template(stringBodyRequest, Method.POST , contentCallback , resultCallback);
    }

    /**
     * post就支持form表单
     * @see StringBodyRequest
     * @see FormRequest
     */
    @Override
    public <R> R form(FormRequest formRequest, ResultCallback<R> resultCallback) throws IOException{
        init(formRequest);
        return post(formRequest , resultCallback);
    }

    @Override
    public <R> R upload(UploadRequest uploadRequest , ResultCallback<R> resultCallback) throws IOException {
        init(uploadRequest);
        ContentCallback<CC> contentCallback = getUploadContentCallbackCreator().create(uploadRequest, Method.POST);
        return template(uploadRequest , Method.POST , contentCallback , resultCallback);
    }

    /*------------------------HTTP方法通用支持---------------------------*/

    @Override
    public <R> R http(HttpRequest httpRequest, Method method, ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest);
        ContentCallback<CC> contentCallback = null;
        if(method.hasContent() && httpRequest instanceof StringBodyRequest){
            contentCallback = getBodyContentCallbackCreator().create(httpRequest , method);
        }
        return template(httpRequest, method , contentCallback, resultCallback);
    }



    /*------------------------HEAD---------------------------*/

    @Override
    public <R> R head(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest);
        //必须要响应头
        httpRequest.setIncludeHeaders(HttpRequest.INCLUDE_HEADERS);
        //设置忽略响应体
        httpRequest.setIgnoreResponseBody(HttpRequest.IGNORE_RESPONSE_BODY);
        return template(httpRequest , Method.HEAD , null , resultCallback);
    }



    /*------------------------OPTIONS---------------------------*/

    @Override
    public <R> R options(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest);
        //必须要响应头
        httpRequest.setIncludeHeaders(HttpRequest.INCLUDE_HEADERS);
        //设置忽略响应体
        httpRequest.setIgnoreResponseBody(HttpRequest.IGNORE_RESPONSE_BODY);
        return template(httpRequest , Method.OPTIONS , null , resultCallback);
    }



    /*------------------------PUT---------------------------*/

    @Override
    public <R> R put(StringBodyRequest stringBodyRequest, ResultCallback<R> resultCallback) throws IOException {
        init(stringBodyRequest);
        ContentCallback<CC> contentCallback = getBodyContentCallbackCreator().create(stringBodyRequest , Method.PUT);
        return template(stringBodyRequest, Method.PUT , contentCallback , resultCallback);
    }



    /*------------------------PATCH---------------------------*/

    @Override
    public <R> R patch(StringBodyRequest stringBodyRequest, ResultCallback<R> resultCallback) throws IOException {
        init(stringBodyRequest);
        ContentCallback<CC> contentCallback = getBodyContentCallbackCreator().create(stringBodyRequest , Method.PATCH);
        return template(stringBodyRequest, Method.PATCH ,contentCallback , resultCallback);
    }



    /*------------------------DELETE---------------------------*/

    @Override
    public <R> R delete(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest);
        return template(httpRequest , Method.DELETE , null , resultCallback);
    }



    /*------------------------TRACE---------------------------*/

    @Override
    public <R> R trace(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        init(httpRequest);
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
        return template(httpRequest, Method.TRACE , null, resultCallback);
    }

    protected void init(HttpRequest httpRequest){
        if(null == httpRequest.getConfig()){
            httpRequest.setConfig(getConfig());
        }
    }

    public BodyContentCallbackCreator<CC> getBodyContentCallbackCreator() {
        return bodyContentCallbackCreator;
    }

    public void setBodyContentCallbackCreator(BodyContentCallbackCreator<CC> bodyContentCallbackCreator) {
        this.bodyContentCallbackCreator = bodyContentCallbackCreator;
    }

    public UploadContentCallbackCreator<CC> getUploadContentCallbackCreator() {
        return uploadContentCallbackCreator;
    }

    public void setUploadContentCallbackCreator(UploadContentCallbackCreator<CC> uploadContentCallbackCreator) {
        this.uploadContentCallbackCreator = uploadContentCallbackCreator;
    }

    public ContentCallbackHandler<CC> getContentCallbackHandler() {
        return contentCallbackHandler;
    }

    public void setContentCallbackHandler(ContentCallbackHandler<CC> contentCallbackHandler) {
        this.contentCallbackHandler = contentCallbackHandler;
    }

    public CompletedUrlCreator getCompletedUrlCreator() {
        return completedUrlCreator;
    }

    public void setCompletedUrlCreator(CompletedUrlCreator completedUrlCreator) {
        this.completedUrlCreator = Objects.requireNonNull(completedUrlCreator);
    }
    /**
     * 处理请求体的回调
     * @param method 请求方法
     * @param body 请求体
     * @param bodyCharset 编码
     * @param contentType Content-Type
     * @return ContentCallback<CC>
     * @throws IOException IOException
     */
    @Override
    protected ContentCallback<CC> bodyContentCallback(Method method , String body , String bodyCharset , String contentType) throws IOException{
        return getBodyContentCallbackCreator().create(method, body, bodyCharset, contentType);
    }

    /**
     * 处理文件上传
     * @param params 参数，k-v，可能为null
     * @param paramCharset 参数编码
     * @param formFiles 文件信息
     * @return ContentCallback<CC>
     * @throws IOException IOException
     */
    @Override
    protected ContentCallback<CC> uploadContentCallback(MultiValueMap<String, String> params , String paramCharset , Iterable<FormFile> formFiles) throws IOException{
        return getUploadContentCallbackCreator().create(params, paramCharset, formFiles);
    }
}

