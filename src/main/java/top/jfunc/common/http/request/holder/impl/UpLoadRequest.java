package top.jfunc.common.http.request.holder.impl;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.holder.DefaultFormFileHolder;
import top.jfunc.common.http.holder.DefaultParamHolder;
import top.jfunc.common.http.holder.FormFileHolder;
import top.jfunc.common.http.holder.ParamHolder;
import top.jfunc.common.http.request.holder.UploadRequest;

/**
 * 多文件、参数同时支持的上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class UpLoadRequest extends BaseRequest<UpLoadRequest> implements UploadRequest {
    public UpLoadRequest(String url){
        super(url);
    }
    public static UpLoadRequest of(String url){
        return new UpLoadRequest(url);
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
    public UpLoadRequest setParamCharset(String paramCharset) {
        formParamHolder().setParamCharset(paramCharset);
        return myself();
    }

    @Override
    public UpLoadRequest addFormParam(String key, String value, String... values) {
        formParamHolder().addParam(key, value, values);
        return myself();
    }

    @Override
    public UpLoadRequest addFormFile(FormFile... formFiles) {
        formFileHolder().addFormFile(formFiles);
        return myself();
    }
}
