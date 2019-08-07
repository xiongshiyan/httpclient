package top.jfunc.common.http.holderrequest.impl;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.holder.DefaultFormFileHolder;
import top.jfunc.common.http.holder.DefaultParamHolder;
import top.jfunc.common.http.holder.FormFileHolder;
import top.jfunc.common.http.holder.ParamHolder;
import top.jfunc.common.http.holderrequest.HolderUploadRequest;

import java.net.URL;

/**
 * 多文件、参数同时支持的上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class HolderUpLoadRequest extends BaseHolderHttpRequest<HolderUpLoadRequest> implements HolderUploadRequest {
    public HolderUpLoadRequest(String url){
        super(url);
    }
    public HolderUpLoadRequest(URL url){
        super(url);
    }
    public HolderUpLoadRequest(){
    }
    public static HolderUpLoadRequest of(){
        return new HolderUpLoadRequest();
    }
    public static HolderUpLoadRequest of(URL url){
        return new HolderUpLoadRequest(url);
    }
    public static HolderUpLoadRequest of(String url){
        return new HolderUpLoadRequest(url);
    }
    /**
     * form参数// private MultiValueMap<String,String> formParamHolder;
     */
    private ParamHolder formParamHolder = new DefaultParamHolder();
    /**
     * 2018-06-18为了文件上传增加的// private List<FormFile> formFiles = null;
     */
    private FormFileHolder formFileHolder = new DefaultFormFileHolder();

    @Override
    public ParamHolder formParamHolder() {
        return formParamHolder;
    }

    @Override
    public FormFileHolder formFileHolder() {
        return formFileHolder;
    }

    @Override
    public HolderUpLoadRequest setParamCharset(String paramCharset) {
        formParamHolder().setParamCharset(paramCharset);
        return myself();
    }

    @Override
    public HolderUpLoadRequest addFormParam(String key, String value, String... values) {
        formParamHolder().addParam(key, value, values);
        return myself();
    }

    @Override
    public HolderUpLoadRequest addFormFile(FormFile... formFiles) {
        formFileHolder().addFormFile(formFiles);
        return myself();
    }
}
