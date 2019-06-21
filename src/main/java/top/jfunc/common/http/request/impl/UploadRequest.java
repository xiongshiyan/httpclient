package top.jfunc.common.http.request.impl;

import top.jfunc.common.http.holder.DefaultFormFileHolder;
import top.jfunc.common.http.holder.DefaultParamHolder;
import top.jfunc.common.http.holder.FormFileHolder;
import top.jfunc.common.http.holder.ParamHolder;

/**
 * 多文件、参数同时支持的上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class UploadRequest extends BaseRequest implements top.jfunc.common.http.request.UploadRequest {
    public UploadRequest(String url){
        super(url);
    }
    public static UploadRequest of(String url){
        return new UploadRequest(url);
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
}
