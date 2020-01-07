package top.jfunc.common.http.smart;

import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.basic.UnpackedParameterHttpClient;
import top.jfunc.common.http.holder.*;
import top.jfunc.common.http.holderrequest.*;
import top.jfunc.common.http.holderrequest.impl.*;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.StrUtil;

import java.io.File;
import java.net.URL;
import java.util.Map;

/**
 * 代表一个Http请求的所有参数,基于Request-Response的可以更好地扩展功能
 * @see UnpackedParameterHttpClient
 * @see HttpRequestResultCallbackHttpClient
 * @see SmartHttpClient
 *
 * !!!此类作为以前的大杂烩，什么样的请求都放到一起，给设置参数的时候造成困扰，已经不适应快速发展的需要
 * 现将其一拆为多，针对不同的请求使用不同的请求即可
 *
 * @see HolderHttpRequest
 * @see BaseHolderHttpRequest
 * @see HolderCommonBodyRequest
 * @see HolderFormBodyRequest
 * @see HolderUpLoadRequest
 * @see HolderDownLoadRequest
 * @author xiongshiyan at 2017/12/9
 *
 * @since 从1.1开始建议不要用此类了,而是使用以上的一些意义更明确的
 */
public class Request extends BaseHolderHttpRequest<Request> implements
        HolderMutableStringBodyRequest,
        HolderFormRequest,
        HolderUploadRequest,
        HolderDownloadRequest {
    /**
     * form参数
     * POST请求，会作为body存在 并且设置Content-Type为 application/xxx-form-url-encoded
     */
    private ParamHolder formParamHolder = new DefaultParamHolder();

    /**
     * 针对POST存在，params这种加进来的参数最终拼接之后保存到这里 private String body
     * @see Method#hasContent()
     */
    private BodyHolder bodyHolder = new DefaultBodyHolder();

    /**
     * 2018-06-18为了文件上传增加的 private List<FormFile> formFiles = null;
     */
    private FormFileHolder formFileHolder = new DefaultFormFileHolder();

    /**
     * 为文件下载确定信息
     */
    private FileHolder fileHolder = new DefaultFileHolder();

    public Request(String url){super(url);}
    public Request(URL url){super(url);}
    public Request(){}

    /**
     * 静态方法创建请求
     * @param url URL
     * @return Request
     */
    public static Request of(String url){
        return new Request(url);
    }
    public static Request of(URL url){
        return new Request(url);
    }
    public static Request of(){
        return new Request();
    }

    /**
     * 如果没有显式设置body而是通过params添加的，此时一般认为是想发起form请求，最好设置Content-Type
     * @see Request#setContentType(String)
     */
    @Override
    public String getBody() {
        String body = bodyHolder.getBody();
        //如果body不为空直接返回
        if(StrUtil.isNotEmpty(body)){
            return body;
        }

        //没有设置body一般认为就是form表单传递
        String bodyCharset = getConfig().calculateBodyCharset(formParamHolder.getParamCharset(), getContentType());
        if(null == getContentType()){
            setContentType(MediaType.APPLICATIPON_FORM_DATA.withCharset(bodyCharset));
        }
        return ParamUtil.contactMap(formParamHolder.getParams(), bodyCharset);
    }


    @Override
    public String getBodyCharset() {
        return StrUtil.isNotEmpty(bodyHolder.getBody()) ? bodyHolder.getBodyCharset() : formParamHolder.getParamCharset();
    }

    @Override
    public FormFileHolder formFileHolder() {
        return this.formFileHolder;
    }

    @Override
    public ParamHolder formParamHolder() {
        return this.formParamHolder;
    }

    @Override
    public FileHolder fileHolder() {
        return this.fileHolder;
    }

    @Override
    public BodyHolder bodyHolder() {
        return this.bodyHolder;
    }

    /**
     * 如果实现的多个接口中有前面完全相同的方法，那么子类中必须复写此方法，相当于解决冲突
     * 明确子类调用此方法时的行为
     * @param paramCharset 参数编码
     * @return this
     */
    @Override
    public Request setParamCharset(String paramCharset) {
        formParamHolder.setParamCharset(paramCharset);
        return this;
    }

    @Override
    public Request setFormParams(Map<String, String> params) {
        formParamHolder.setParams(params);
        return this;
    }
    @Override
    public Request setFormParams(MultiValueMap<String, String> params) {
        formParamHolder.setParams(params);
        return this;
    }

    @Override
    public Request addFormParam(String key, String value, String... values) {
        formParamHolder.addParam(key, value, values);
        return this;
    }

    @Override
    public MultiValueMap<String, String> getFormParams() {
        return formParamHolder.getParams();
    }

    /**
     * 设置body的同时设置Content-Type
     * @see MediaType
     */
    @Override
    public Request setBody(String body , String contentType) {
        bodyHolder.setBody(body);
        setContentType(contentType);
        return this;
    }


    ///////////// 以下方法是为了兼容以前的Request的用法[返回Request方便方法连缀] /////////////////////

    @Override
    public Request setBody(String body) {
        this.bodyHolder.setBody(body);
        return this;
    }

    /**
     * 可能是form的，可能是body的，最终其实都是body，
     * 如果想单独设置，请使用分别的holder引用来设置
     * @param bodyCharset bodyCharset
     * @return this
     */
    @Override
    public Request setBodyCharset(String bodyCharset) {
        bodyHolder.setBodyCharset(bodyCharset);
        formParamHolder.setParamCharset(bodyCharset);
        return this;
    }

    @Override
    public Request addFormFile(FormFile... formFiles) {
        formFileHolder.addFormFile(formFiles);
        return this;
    }

    @Override
    public Request addFormFiles(Iterable<FormFile> formFiles) {
        formFileHolder.addFormFiles(formFiles);
        return this;
    }

    @Override
    public Request setFile(File file) {
        fileHolder.setFile(file);
        return this;
    }

    /////////////////////////////////// END /////////////////////////////////////
}
