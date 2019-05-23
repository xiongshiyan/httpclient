package top.jfunc.common.http.smart;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.handler.ToString;
import top.jfunc.common.http.base.handler.ToStringHandler;
import top.jfunc.common.http.kv.Parameter;
import top.jfunc.common.http.request.DownLoadRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.impl.BaseRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.StrUtil;

import java.io.File;
import java.util.*;

/**
 * 代表一个Http请求的所有参数,基于Request-Response的可以更好地扩展功能
 * @see top.jfunc.common.http.basic.HttpClient
 * @see SmartHttpClient
 *
 * !!!此类作为以前的大杂烩，什么样的请求都放到一起，给设置参数的时候造成困扰，已经不适应快速发展的需要
 * 现将其一拆为多，针对不同的请求使用不同的请求即可
 *
 * @see HttpRequest
 *
 * @see BaseRequest
 * @see top.jfunc.common.http.request.impl.PostBodyRequest
 * @see top.jfunc.common.http.request.impl.FormBodyRequest
 * @see top.jfunc.common.http.request.impl.FileParamUploadRequest
 * @see top.jfunc.common.http.request.impl.DownLoadRequest
 * @author xiongshiyan at 2017/12/9
 *
 * @since 从1.1开始建议不要用此类了,而是使用以上的一些意义更明确的
 */
public class Request extends BaseRequest<Request> implements StringBodyRequest, UploadRequest, DownLoadRequest {
    /**
     * form参数
     * POST请求，会作为body存在 并且设置Content-Type为 application/xxx-form-url-encoded
     */
    private MultiValueMap<String,String> formParams;
    /**
     * 针对POST存在，params这种加进来的参数最终拼接之后保存到这里
     * @see Method#hasContent()
     */
    private String body;
    /**
     * 请求体编码，不设置就使用系统默认的
     * @see top.jfunc.common.http.base.Config#defaultBodyCharset
     */
    private String bodyCharset = HttpConstants.DEFAULT_CHARSET;


    /**
     * 2018-06-18为了文件上传增加的
     */
    private List<FormFile> formFiles = null;
    /**
     * 为文件下载确定信息
     */
    private File file = null;

    public Request(String url){super(url);}

    /**
     * 静态方法创建请求
     * @param url URL
     * @return Request
     */
    public static Request of(String url){
        return new Request(url);
    }

    /****************************Getter**************************/
    @Override
    public MultiValueMap<String, String> getFormParams() {
        return formParams;
    }

    /**
     * 如果没有显式设置body而是通过params添加的，此时一般认为是想发起form请求，最好设置Content-Type
     * @see this#setContentType(String)
     */
    @Override
    public String getBody() {
        //如果没有Body就将params的参数拼接
        if(StrUtil.isBlank(body)){
            //没有显式设置就设置默认的
            if(null == getContentType()){
                setContentType(MediaType.APPLICATIPON_FORM_DATA.withCharset(bodyCharset));
            }
            return ParamUtil.contactMap(formParams, bodyCharset);
        }
        //直接返回设置的body
        return body;
    }
    @Override
    public FormFile[] getFormFiles() {
        initFormFiles();
        return this.formFiles.toArray(new FormFile[this.formFiles.size()]);
    }
    private void initFormFiles(){
        if(null == this.formFiles){
            this.formFiles = new ArrayList<>(2);
        }
    }

    @Override
    public File getFile() {
        return file;
    }

    /**************************变种的Setter*******************************/

    public Request setFormParams(MultiValueMap<String, String> formParams) {
        this.formParams = Objects.requireNonNull(formParams);
        return this;
    }
    public Request setFormParams(ArrayListMultimap<String, String> formParams) {
        Objects.requireNonNull(formParams);
        this.formParams = ArrayListMultiValueMap.fromMap(formParams);
        return this;
    }
    public Request setFormParams(Map<String, String> formParams) {
        Objects.requireNonNull(formParams);
        this.formParams = ArrayListMultiValueMap.fromMap(formParams);
        return this;
    }
    public Request addFormParam(String key, String value){
        initFormParams();
        this.formParams.add(key, value);
        return this;
    }
    public Request addFormParam(String key, String value , String... values){
        initFormParams();
        this.formParams.add(key , value);
        for (String val : values) {
            this.formParams.add(key , val);
        }
        return this;
    }
    public Request addFormParam(String key, Iterable<String> values){
        initFormParams();
        for (String value : values) {
            this.formParams.add(key , value);
        }
        return this;
    }
    public Request addFormParam(Parameter parameter , Parameter... parameters){
        addFormParam(parameter.getKey() , parameter.getValue());
        for (Parameter param : parameters) {
            addFormParam(param.getKey() , param.getValue());
        }
        return this;
    }
    public Request addFormParam(Iterable<Parameter> parameters){
        for (Parameter parameter : parameters) {
            addFormParam(parameter.getKey() , parameter.getValue());
        }
        return this;
    }
    public Request addFormParam(Map.Entry<String , Iterable<String>> parameter , Map.Entry<String , Iterable<String>>... parameters){
        addFormParam(parameter.getKey() , parameter.getValue());
        for (Map.Entry<String , Iterable<String>> param : parameters) {
            addFormParam(param.getKey() , param.getValue());
        }
        return this;
    }
    private void initFormParams(){
        if(null == this.formParams){
            this.formParams = new ArrayListMultiValueMap<>(2);
        }
    }

    /**
     * 设置body,最好是调用{@link this#setBody(String, String)}同时设置Content-Type
     */
    public Request setBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * 设置body的同时设置Content-Type
     * @see MediaType
     */
    public Request setBody(String body , String contentType) {
        this.body = body;
        setContentType(contentType);
        return this;
    }

    /**
     * 直接传输一个Java对象可以使用该方法
     * @param o Java对象
     * @param handler 将Java对象转换为String的策略接口
     * @return this
     */
    public <T> Request setBody(T o , ToStringHandler<T> handler){
        ToStringHandler<T> stringHandler = Objects.requireNonNull(handler, "handler不能为空");
        this.body = stringHandler.toString(o);
        return this;
    }
    public Request setBodyT(Object o , ToString handler){
        ToString toString = Objects.requireNonNull(handler, "handler不能为空");
        this.body = toString.toString(o);
        return this;
    }

    public Request addFormFile(FormFile... formFiles) {
        if(null != formFiles){
            initFormFiles();
            this.formFiles.addAll(Arrays.asList(formFiles));
        }
        return this;
    }

    public Request setFile(File file) {
        this.file = file;
        return this;
    }
    public Request setFile(String filePath) {
        this.file = new File(filePath);
        return this;
    }
}
