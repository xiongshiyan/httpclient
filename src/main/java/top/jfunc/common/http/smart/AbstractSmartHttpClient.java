package top.jfunc.common.http.smart;

import top.jfunc.common.http.base.*;
import top.jfunc.common.http.component.BodyContentCallbackCreator;
import top.jfunc.common.http.component.UploadContentCallbackCreator;
import top.jfunc.common.http.component.httprequest.*;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.response.ResponseConverter;
import top.jfunc.common.http.response.ResponseExtractor;
import top.jfunc.common.utils.*;

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
public abstract class AbstractSmartHttpClient<CC> implements SmartHttpClient, SmartHttpTemplate<CC> {
    /**一堆参数组装为{@link HttpRequest}*/
    private HttpRequestFactory httpRequestFactory;
    /**一堆参数组装为{@link StringBodyRequest}*/
    private StringBodyHttpRequestFactory stringBodyHttpRequestFactory;
    /**一堆参数组装为{@link UploadRequest}*/
    private UploadRequestFactory uploadRequestFactory;
    /**处理一般包含body的*/
    private BodyContentCallbackCreator<CC> bodyContentCallbackCreator;
    /**处理文件上传*/
    private UploadContentCallbackCreator<CC> uploadContentCallbackCreator;

    /**配置冻结器*/
    private ConfigFrozen configFrozen = new ConfigFrozen();
    /**保存系统默认配置*/
    private Config config = Config.defaultConfig();

    protected AbstractSmartHttpClient(BodyContentCallbackCreator<CC> bodyContentCallbackCreator,
                                      UploadContentCallbackCreator<CC> uploadContentCallbackCreator){
        this.httpRequestFactory = new DefaultHttpRequestFactory();
        this.stringBodyHttpRequestFactory = new DefaultStringBodyHttpRequestFactory();
        this.uploadRequestFactory = new DefaultUploadRequestFactory();

        this.bodyContentCallbackCreator = bodyContentCallbackCreator;
        this.uploadContentCallbackCreator = uploadContentCallbackCreator;
    }
    protected AbstractSmartHttpClient(HttpRequestFactory httpRequestFactory,
                                      StringBodyHttpRequestFactory stringBodyHttpRequestFactory,
                                      UploadRequestFactory uploadRequestFactory,
                                      BodyContentCallbackCreator<CC> bodyContentCallbackCreator,
                                      UploadContentCallbackCreator<CC> uploadContentCallbackCreator){
        this.httpRequestFactory = httpRequestFactory;
        this.stringBodyHttpRequestFactory = stringBodyHttpRequestFactory;
        this.uploadRequestFactory = uploadRequestFactory;

        this.bodyContentCallbackCreator = bodyContentCallbackCreator;
        this.uploadContentCallbackCreator = uploadContentCallbackCreator;
    }

    @Override
    public <R> R http(HttpRequest httpRequest, Method method, ResponseConverter<R> responseConverter) throws IOException {
        init(httpRequest , method);
        ContentCallback<CC> contentCallback = getBodyContentCallbackCreator().create(httpRequest);
        return template(httpRequest, contentCallback, responseConverter);
    }

    @Override
    public <R> R upload(UploadRequest uploadRequest , ResponseConverter<R> responseConverter) throws IOException {
        init(uploadRequest, Method.POST);
        ContentCallback<CC> contentCallback = getUploadContentCallbackCreator().create(uploadRequest);
        return template(uploadRequest, contentCallback, responseConverter);
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
        return get(httpRequest , ResponseExtractor::extractString);
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) throws IOException {
        StringBodyRequest stringBodyRequest = getStringBodyHttpRequestFactory().create(url, body, contentType, headers, connectTimeout, readTimeout, bodyCharset, resultCharset);
        return post(stringBodyRequest , ResponseExtractor::extractString);
    }

    @Override
    public byte[] getAsBytes(String url, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout) throws IOException {
        HttpRequest httpRequest = getHttpRequestFactory().create(url, null, headers, connectTimeout, readTimeout, null);
        return get(httpRequest , ResponseExtractor::extractBytes);
    }

    @Override
    public File getAsFile(String url, MultiValueMap<String, String> headers, File file, int connectTimeout, int readTimeout) throws IOException {
        HttpRequest httpRequest = getHttpRequestFactory().create(url, null, headers, connectTimeout, readTimeout, null);
        return get(httpRequest , (clientHttpResponse, resultCharset)-> IoUtil.copy2File(clientHttpResponse.getBody(), file));
    }


    @Override
    public String upload(String url, MultiValueMap<String,String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) throws IOException{
        UploadRequest uploadRequest = getUploadRequestFactory().create(url, null, headers, connectTimeout, readTimeout, resultCharset, files);
        return upload(uploadRequest , ResponseExtractor::extractString);
    }

    @Override
    public String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) throws IOException {
        UploadRequest uploadRequest = getUploadRequestFactory().create(url, params, headers, connectTimeout, readTimeout, resultCharset, files);
        return upload(uploadRequest , ResponseExtractor::extractString);
    }


    protected String calculateResultCharset(HttpRequest httpRequest) {
        Config config = getConfig();
        return ObjectUtil.defaultIfNull(httpRequest.getResultCharset(), config.getDefaultResultCharset());
    }

    public BodyContentCallbackCreator<CC> getBodyContentCallbackCreator() {
        return bodyContentCallbackCreator;
    }

    public UploadContentCallbackCreator<CC> getUploadContentCallbackCreator() {
        return uploadContentCallbackCreator;
    }

    public HttpRequestFactory getHttpRequestFactory() {
        return httpRequestFactory;
    }

    public StringBodyHttpRequestFactory getStringBodyHttpRequestFactory() {
        return stringBodyHttpRequestFactory;
    }

    public UploadRequestFactory getUploadRequestFactory() {
        return uploadRequestFactory;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public void setConfig(Config config) {
        configFrozen.ensureConfigNotFreeze();
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public void freezeConfig() {
        //本身冻结
        configFrozen.freezeConfig();
        //Config冻结
        config.freezeConfig();
    }
}

