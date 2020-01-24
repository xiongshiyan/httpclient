package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.AbstractConfigurableHttp;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.component.httprequest.*;
import top.jfunc.common.http.cookie.CookieAccessor;
import top.jfunc.common.http.cookie.DefaultCookieAccessor;
import top.jfunc.common.http.request.FormRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.util.ResponseUtil;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 提供了{@link SmartHttpClient}接口的实现
 * @see SimpleHttpClient
 * @see HttpRequestHttpClient
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

    private HttpRequestFactory httpRequestFactory;
    private StringBodyHttpRequestFactory stringBodyHttpRequestFactory;
    private UploadRequestFactory uploadRequestFactory;

    /**处理Cookie*/
    private CookieAccessor cookieAccessor;

    public AbstractSmartHttpClient(){
        setContentCallbackHandler(new DefaultContentCallbackHandler<>());
        setInputStreamCloser(new DefaultCloser());

        setHttpRequestFactory(new DefaultHttpRequestFactory());
        setStringBodyHttpRequestFactory(new DefaultStringBodyHttpRequestFactory());
        setUploadRequestFactory(new DefaultUploadRequestFactory());

        setCookieAccessor(new DefaultCookieAccessor());
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


    /////////////////////////////////////////////////////SimpleHttpClient///////////////////////////////////////////////////////////////

    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout, String resultCharset) throws IOException{
        MultiValueMap<String , String> p = null;
        if(MapUtil.notEmpty(params)){
            p = ArrayListMultiValueMap.fromMap(params);
        }
        MultiValueMap<String , String> h = null;
        if(MapUtil.notEmpty(headers)){
            h = ArrayListMultiValueMap.fromMap(headers);
        }
        HttpRequest httpRequest = getHttpRequestFactory().create(url, p, h, connectTimeout, readTimeout, resultCharset);
        return get(httpRequest , ResponseUtil::extractString);
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) throws IOException {
        StringBodyRequest stringBodyRequest = getStringBodyHttpRequestFactory().create(url, body, contentType, headers, connectTimeout, readTimeout, bodyCharset, resultCharset);
        return post(stringBodyRequest , ResponseUtil::extractString);
    }

    @Override
    public byte[] getAsBytes(String url, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout) throws IOException {
        HttpRequest httpRequest = getHttpRequestFactory().create(url, null, headers, connectTimeout, readTimeout, null);
        return get(httpRequest , ResponseUtil::extractBytes);
    }

    @Override
    public File getAsFile(String url, MultiValueMap<String, String> headers, File file, int connectTimeout, int readTimeout) throws IOException {
        HttpRequest httpRequest = getHttpRequestFactory().create(url, null, headers, connectTimeout, readTimeout, null);
        return get(httpRequest , (statusCode, inputStream, rc, hd)-> IoUtil.copy2File(inputStream, file));
    }


    @Override
    public String upload(String url, MultiValueMap<String,String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) throws IOException{
        UploadRequest uploadRequest = getUploadRequestFactory().create(url, null, headers, connectTimeout, readTimeout, resultCharset, files);
        return upload(uploadRequest , ResponseUtil::extractString);
    }

    @Override
    public String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) throws IOException {
        UploadRequest uploadRequest = getUploadRequestFactory().create(url, params, headers, connectTimeout, readTimeout, resultCharset, files);
        return upload(uploadRequest , ResponseUtil::extractString);
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

    public HttpRequestFactory getHttpRequestFactory() {
        return httpRequestFactory;
    }

    public void setHttpRequestFactory(HttpRequestFactory httpRequestFactory) {
        this.httpRequestFactory = Objects.requireNonNull(httpRequestFactory);
    }

    public StringBodyHttpRequestFactory getStringBodyHttpRequestFactory() {
        return stringBodyHttpRequestFactory;
    }

    public void setStringBodyHttpRequestFactory(StringBodyHttpRequestFactory stringBodyHttpRequestFactory) {
        this.stringBodyHttpRequestFactory = Objects.requireNonNull(stringBodyHttpRequestFactory);
    }

    public UploadRequestFactory getUploadRequestFactory() {
        return uploadRequestFactory;
    }

    public void setUploadRequestFactory(UploadRequestFactory uploadRequestFactory) {
        this.uploadRequestFactory = Objects.requireNonNull(uploadRequestFactory);
    }

    public CookieAccessor getCookieAccessor() {
        return cookieAccessor;
    }

    public void setCookieAccessor(CookieAccessor cookieAccessor) {
        this.cookieAccessor = Objects.requireNonNull(cookieAccessor);
    }
}

