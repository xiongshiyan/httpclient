package top.jfunc.common.http.smart;

import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ProxyInfo;
import top.jfunc.common.http.holder.*;
import top.jfunc.common.http.request.DownLoadRequest;
import top.jfunc.common.http.request.MutableStringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.impl.BaseRequest;
import top.jfunc.common.utils.StrUtil;

import java.io.File;

/**
 * 代表一个Http请求的所有参数,基于Request-Response的可以更好地扩展功能
 * @see top.jfunc.common.http.basic.HttpClient
 * @see top.jfunc.common.http.smart.SmartHttpClient
 *
 * !!!此类作为以前的大杂烩，什么样的请求都放到一起，给设置参数的时候造成困扰，已经不适应快速发展的需要
 * 现将其一拆为多，针对不同的请求使用不同的请求即可
 *
 * @see top.jfunc.common.http.request.HttpRequest
 * @see top.jfunc.common.http.request.impl.BaseRequest
 * @see top.jfunc.common.http.request.impl.PostBodyRequest
 * @see top.jfunc.common.http.request.impl.FormBodyRequest
 * @see top.jfunc.common.http.request.impl.FileParamUploadRequest
 * @see top.jfunc.common.http.request.impl.DownLoadRequest
 * @author xiongshiyan at 2017/12/9
 *
 * @since 从1.1开始建议不要用此类了,而是使用以上的一些意义更明确的
 */
public class Request extends BaseRequest<Request> implements
        MutableStringBodyRequest,
        UploadRequest,
        DownLoadRequest {
    /**
     * form参数
     * POST请求，会作为body存在 并且设置Content-Type为 application/xxx-form-url-encoded
     * //private MultiValueMap<String,String> formParamHolder;
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

    /**
     * 静态方法创建请求
     * @param url URL
     * @return Request
     */
    public static Request of(String url){
        return new Request(url);
    }

    /**
     * 如果没有显式设置body而是通过params添加的，此时一般认为是想发起form请求，最好设置Content-Type
     * @see Request#setContentType(String)
     */
    @Override
    public String getBody() {
        String body = bodyHolder.getBody();
        //如果没有Body就将params的参数拼接
        if(StrUtil.isBlank(body)){
            //没有显式设置就设置默认的
            String bodyCharset = formParamHolder.getParamCharset();
            if(null == getContentType()){
                setContentType(MediaType.APPLICATIPON_FORM_DATA.withCharset(bodyCharset));
            }
            return ParamUtil.contactMap(formParamHolder.getParams(), bodyCharset);
        }
        //直接返回设置的body
        return body;
    }


    @Override
    public String getBodyCharset() {
        return StrUtil.isNotBlank(bodyHolder.getBody()) ? bodyHolder.getBodyCharset() : formParamHolder.getParamCharset();
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
    public Request addRouteParam(String key, String value) {
        super.addRouteParam(key, value);
        return this;
    }

    @Override
    public Request addHeader(String key, String value , String... values){
        super.addHeader(key, value, values);
        return this;
    }

    @Override
    public Request addQueryParam(String key, String value , String... values){
        super.addQueryParam(key, value, values);
        return this;
    }

    @Override
    public Request addFormParam(String key, String value, String... values) {
        this.formParamHolder.addParam(key, value, values);
        return this;
    }

    @Override
    public Request setBody(String body) {
        this.bodyHolder.setBody(body);
        return this;
    }

    @Override
    public Request setContentType(String contentType) {
        super.setContentType(contentType);
        return this;
    }

    @Override
    public Request setConnectionTimeout(int connectionTimeout) {
        super.setConnectionTimeout(connectionTimeout);
        return this;
    }

    @Override
    public Request setReadTimeout(int readTimeout) {
        super.setReadTimeout(readTimeout);
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
    public Request setResultCharset(String resultCharset) {
        super.setResultCharset(resultCharset);
        return this;
    }

    @Override
    public Request setIncludeHeaders(boolean includeHeaders) {
        super.setIncludeHeaders(includeHeaders);
        return this;
    }

    @Override
    public Request setIgnoreResponseBody(boolean ignoreResponseBody) {
        super.setIgnoreResponseBody(ignoreResponseBody);
        return this;
    }

    @Override
    public Request setRedirectable(boolean redirectable) {
        super.setRedirectable(redirectable);
        return this;
    }

    @Override
    public Request addFormFile(FormFile... formFiles) {
        formFileHolder.addFormFile(formFiles);
        return this;
    }

    @Override
    public Request setFile(File file) {
        fileHolder.setFile(file);
        return this;
    }

    @Override
    public Request setProxy(ProxyInfo proxyInfo) {
        super.setProxy(proxyInfo);
        return this;
    }

    /////////////////////////////////// END /////////////////////////////////////
}
