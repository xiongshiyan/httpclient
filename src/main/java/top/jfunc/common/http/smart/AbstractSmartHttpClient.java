package top.jfunc.common.http.smart;

import top.jfunc.common.http.base.*;
import top.jfunc.common.http.component.*;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.response.ClientHttpResponseConverter;
import top.jfunc.common.http.template.SmartHttpTemplate;
import top.jfunc.common.http.util.ResponseExtractor;
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
    /**一堆参数组装为{@link HttpRequest}、{@link StringBodyRequest}、{@link UploadRequest}*/
    private AssemblingFactory assemblingFactory;

    /**处理一般包含body的*/
    private ContentCallbackCreator<CC> bodyContentCallbackCreator;
    /**处理文件上传*/
    private ContentCallbackCreator<CC> uploadContentCallbackCreator;

    /**配置冻结器*/
    private ConfigFrozen configFrozen = new ConfigFrozen();
    /**保存系统默认配置*/
    private Config config = Config.defaultConfig();

    protected AbstractSmartHttpClient(ContentCallbackCreator<CC> bodyContentCallbackCreator,
                                      ContentCallbackCreator<CC> uploadContentCallbackCreator){
        this.assemblingFactory = new DefaultSimpleAssemblingFactory();
        this.bodyContentCallbackCreator = bodyContentCallbackCreator;
        this.uploadContentCallbackCreator = uploadContentCallbackCreator;
    }
    protected AbstractSmartHttpClient(AssemblingFactory assemblingFactory,
                                      ContentCallbackCreator<CC> bodyContentCallbackCreator,
                                      ContentCallbackCreator<CC> uploadContentCallbackCreator){
        this.assemblingFactory = assemblingFactory;
        this.bodyContentCallbackCreator = bodyContentCallbackCreator;
        this.uploadContentCallbackCreator = uploadContentCallbackCreator;
    }

    @Override
    public <R> R http(HttpRequest httpRequest, Method method, ClientHttpResponseConverter<R> clientHttpResponseConverter) throws IOException {
        init(httpRequest , method);
        ContentCallback<CC> contentCallback = getBodyContentCallbackCreator().create(httpRequest);
        return template(httpRequest, contentCallback, clientHttpResponseConverter);
    }

    @Override
    public <R> R upload(UploadRequest uploadRequest , ClientHttpResponseConverter<R> clientHttpResponseConverter) throws IOException {
        init(uploadRequest, Method.POST);
        ContentCallback<CC> contentCallback = getUploadContentCallbackCreator().create(uploadRequest);
        return template(uploadRequest, contentCallback, clientHttpResponseConverter);
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
    public String get(String url, Map<String, String> queryParams, Map<String, String> headers,
                      int connectTimeout, int readTimeout, String resultCharset) throws IOException{
        MultiValueMap<String , String> q = ArrayListMultiValueMap.fromMap(queryParams);
        MultiValueMap<String , String> h = ArrayListMultiValueMap.fromMap(headers);
        HttpRequest httpRequest = getAssemblingFactory()
                .create(url, q, h, connectTimeout, readTimeout, resultCharset);
        return get(httpRequest , ResponseExtractor::extractString);
    }

    @Override
    public String post(String url, Map<String, String> queryParams, String body, String contentType, Map<String, String> headers,
                       int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) throws IOException {
        MultiValueMap<String , String> q = ArrayListMultiValueMap.fromMap(queryParams);
        MultiValueMap<String , String> h = ArrayListMultiValueMap.fromMap(headers);
        StringBodyRequest stringBodyRequest = getAssemblingFactory()
                .create(url, q, body, contentType, h, connectTimeout, readTimeout, bodyCharset, resultCharset);
        return post(stringBodyRequest , ResponseExtractor::extractString);
    }

    @Override
    public byte[] getAsBytes(String url, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout) throws IOException {
        HttpRequest httpRequest = getAssemblingFactory()
                .create(url, null, headers, connectTimeout, readTimeout, null);
        return get(httpRequest , ResponseExtractor::extractBytes);
    }

    @Override
    public File getAsFile(String url, MultiValueMap<String, String> headers, File file, int connectTimeout, int readTimeout) throws IOException {
        HttpRequest httpRequest = getAssemblingFactory()
                .create(url, null, headers, connectTimeout, readTimeout, null);
        return get(httpRequest, (chr, r) -> IoUtil.copy2File(chr.getBody(), file));
    }


    @Override
    public String upload(String url, MultiValueMap<String,String> headers,
                         int connectTimeout, int readTimeout, String resultCharset, FormFile... formFiles) throws IOException{
        UploadRequest uploadRequest = getAssemblingFactory()
                .create(url, null, formFiles, headers, connectTimeout, readTimeout, resultCharset);
        return upload(uploadRequest , ResponseExtractor::extractString);
    }

    @Override
    public String upload(String url, MultiValueMap<String, String> formParams, MultiValueMap<String, String> headers,
                         int connectTimeout, int readTimeout, String resultCharset, FormFile... formFiles) throws IOException {
        UploadRequest uploadRequest = getAssemblingFactory()
                .create(url, formParams, formFiles, headers, connectTimeout, readTimeout, resultCharset);
        return upload(uploadRequest , ResponseExtractor::extractString);
    }


    protected String calculateResultCharset(HttpRequest httpRequest) {
        Config config = getConfig();
        return ObjectUtil.defaultIfNull(httpRequest.getResultCharset(), config.getDefaultResultCharset());
    }

    public ContentCallbackCreator<CC> getBodyContentCallbackCreator() {
        return bodyContentCallbackCreator;
    }

    public ContentCallbackCreator<CC> getUploadContentCallbackCreator() {
        return uploadContentCallbackCreator;
    }

    public AssemblingFactory getAssemblingFactory() {
        return assemblingFactory;
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

