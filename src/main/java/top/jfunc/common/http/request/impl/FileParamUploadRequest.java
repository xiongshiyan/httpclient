package top.jfunc.common.http.request.impl;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.kv.DefaultParamHolder;
import top.jfunc.common.http.kv.ParamHolder;
import top.jfunc.common.http.request.UploadRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 多文件、参数同时支持的上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class FileParamUploadRequest extends BaseRequest<FileParamUploadRequest> implements UploadRequest {
    public FileParamUploadRequest(String url){
        super(url);
    }
    public static FileParamUploadRequest of(String url){
        return new FileParamUploadRequest(url);
    }
    /**
     * form参数
     */
    //private MultiValueMap<String,String> formParamHolder;
    private ParamHolder formParamHolder = new DefaultParamHolder();
    /**
     * 2018-06-18为了文件上传增加的
     */
    private List<FormFile> formFiles = null;

    @Override
    public ParamHolder formParamHolder() {
        return formParamHolder;
    }

    @Override
    public FormFile[] getFormFiles() {
        initFormFiles();
        return this.formFiles.toArray(new FormFile[this.formFiles.size()]);
    }

    public FileParamUploadRequest setFormFiles(List<FormFile> formFiles) {
        this.formFiles = Objects.requireNonNull(formFiles);
        return this;
    }

    @Override
    public FileParamUploadRequest addFormFile(FormFile... formFiles) {
        if(null != formFiles){
            initFormFiles();
            this.formFiles.addAll(Arrays.asList(formFiles));
        }
        return this;
    }

    private void initFormFiles(){
        if(null == this.formFiles){
            this.formFiles = new ArrayList<>(2);
        }
    }
}
