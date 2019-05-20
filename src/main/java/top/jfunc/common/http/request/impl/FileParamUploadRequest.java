package top.jfunc.common.http.request.impl;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.kv.Parameter;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.MultiValueMap;

import java.util.*;

/**
 * 多文件、参数同时支持的上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class FileParamUploadRequest extends BaseRequest<FileParamUploadRequest> implements UploadRequest {
    public FileParamUploadRequest(String url){
        super(url);
    }
    public FileParamUploadRequest(){}

    public static FileParamUploadRequest of(String url){
        return new FileParamUploadRequest(url);
    }
    /**
     * form参数
     */
    private MultiValueMap<String,String> formParams;
    /**
     * 2018-06-18为了文件上传增加的
     */
    private List<FormFile> formFiles = null;

    @Override
    public MultiValueMap<String, String> getFormParams() {
        return formParams;
    }

    @Override
    public FormFile[] getFormFiles() {
        initFormFiles();
        return this.formFiles.toArray(new FormFile[this.formFiles.size()]);
    }

    public FileParamUploadRequest setFormParams(MultiValueMap<String, String> formParams) {
        this.formParams = Objects.requireNonNull(formParams);
        return this;
    }
    public FileParamUploadRequest setFormParams(ArrayListMultimap<String, String> formParams) {
        Objects.requireNonNull(formParams);
        this.formParams = ArrayListMultiValueMap.fromMap(formParams);
        return this;
    }
    public FileParamUploadRequest setFormParams(Map<String, String> formParams) {
        Objects.requireNonNull(formParams);
        this.formParams = ArrayListMultiValueMap.fromMap(formParams);
        return this;
    }
    public FileParamUploadRequest addFormParam(String key, String value){
        initFormParams();
        this.formParams.add(key, value);
        return this;
    }
    public FileParamUploadRequest addFormParam(String key, String... values){
        initFormParams();
        for (String value : values) {
            this.formParams.add(key , value);
        }
        return this;
    }
    public FileParamUploadRequest addFormParam(String key, Iterable<String> values){
        initFormParams();
        for (String value : values) {
            this.formParams.add(key , value);
        }
        return this;
    }
    public FileParamUploadRequest addFormParam(Parameter... parameters){
        for (Parameter parameter : parameters) {
            addFormParam(parameter.getKey() , parameter.getValue());
        }
        return this;
    }
    public FileParamUploadRequest addFormParam(Iterable<Parameter> parameters){
        for (Parameter parameter : parameters) {
            addFormParam(parameter.getKey() , parameter.getValue());
        }
        return this;
    }
    public FileParamUploadRequest addFormParam(Map.Entry<String , Iterable<String>>... parameters){
        for (Map.Entry<String , Iterable<String>> parameter : parameters) {
            addFormParam(parameter.getKey() , parameter.getValue());
        }
        return this;
    }
    private void initFormParams(){
        if(null == this.formParams){
            this.formParams = new ArrayListMultiValueMap<>(2);
        }
    }

    public FileParamUploadRequest setFormFiles(List<FormFile> formFiles) {
        this.formFiles = Objects.requireNonNull(formFiles);
        return this;
    }

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
