package top.jfunc.common.http.smart;

import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.basic.FormFile;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.StrUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static top.jfunc.common.http.HttpConstants.*;

/**
 * @author xiongshiyan at 2017/12/9
 */
public class Request {
    /**
     * 请求的URL
     */
    private String url;
    /**
     * 请求参数，针对GET请求存在
     */
    private ArrayListMultimap<String,String> params = new ArrayListMultimap<>();
    /**
     * 请求头
     */
    private ArrayListMultimap<String,String> headers = new ArrayListMultimap<>();
    /**
     * 针对POST存在，params这种加进来的参数最终拼接之后保存到这里
     */
    private String body;
    /**
     * 资源类型
     */
    private String contentType = null;
    /**
     * 连接超时时间
     */
    private Integer connectionTimeout = null;
    /**
     * 读数据超时时间
     */
    private Integer readTimeout = null;
    /**
     * 请求体编码
     */
    private String bodyCharset = null;
    /**
     * 返回体编码
     */
    private String resultCharset = null;

    /**
     * 返回结果中是否包含headers
     */
    private boolean includeHeaders = false;

    /**
     * 是否支持重定向
     */
    private boolean redirectable = false;

    /**
     * 2018-06-18为了文件上传增加的
     */
    private List<FormFile> formFiles = new ArrayList<>();
    /**
     * 为文件下载确定信息
     */
    private File file = null;

    public Request(String url){this.url = url;}

    /**
     * 静态方法创建请求
     * @param url URL
     * @return Request
     */
    public static Request of(String url){
        return new Request(url);
    }

    /**************************变种的Setter*******************************/
    public Request setUrl(String url) {
        this.url = url;
        return this;
    }

    public Request setParams(ArrayListMultimap<String, String> params) {
        this.params = params;
        return this;
    }
    public Request setParams(Map<String, String> params) {
        params.forEach((k,v)->this.params.put(k,v));
        return this;
    }
    public Request addParam(String key, String value){
        this.params.put(key, value);
        return this;
    }
    public Request setHeaders(ArrayListMultimap<String, String> headers) {
        this.headers = headers;
        return this;
    }
    public Request setHeaders(Map<String, String> headers) {
        headers.forEach((k,v)->this.headers.put(k,v));
        return this;
    }
    public Request addHeader(String key, String value){
        this.headers.put(key, value);
        return this;
    }

    public Request setBody(String body) {
        this.body = body;
        return this;
    }

    public Request setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Request setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public Request setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }
    public Request setBodyCharset(String bodyCharset) {
        this.bodyCharset = bodyCharset;
        return this;
    }

    public Request setResultCharset(String resultCharset) {
        this.resultCharset = resultCharset;
        return this;
    }

    public Request setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
        return this;
    }

    public Request setRedirectable(boolean redirectable) {
        this.redirectable = redirectable;
        //要支持重定向必须header
        this.includeHeaders = true;
        return this;
    }

    public Request addFormFile(FormFile... formFiles) {
        if(null != formFiles){
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

    /****************************Getter**************************/
    public String getUrl() {
        return url;
    }

    public ArrayListMultimap<String, String> getParams() {
        return params;
    }

    public ArrayListMultimap<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        //如果没有Body就将params的参数拼接
        if(StrUtil.isBlank(body)){
            return ParamUtil.contactMap(params , bodyCharset);
        }
        return body;
    }

    public String getContentType() {
        return contentType;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public String getBodyCharset() {
        return bodyCharset;
    }

    public String getResultCharset() {
        return resultCharset;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public boolean isRedirectable() {
        return redirectable;
    }

    public FormFile[] getFormFiles() {
        return this.formFiles.toArray(new FormFile[this.formFiles.size()]);
    }

    public File getFile() {
        return file;
    }
}
