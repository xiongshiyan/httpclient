package top.jfunc.common.http.req.impl;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.req.UploadRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private MultiValueMap<String , String> formParams = new ArrayListMultiValueMap<>(2);
    private String formParamCharset = HttpConstants.DEFAULT_CHARSET;
    private List<FormFile> formFiles = new ArrayList<>(2);

    @Override
    public FormFile[] getFormFiles() {
        return this.formFiles.toArray(new FormFile[this.formFiles.size()]);
    }

    @Override
    public MultiValueMap<String, String> getFormParams() {
        return formParams;
    }

    @Override
    public UpLoadRequest addFormParam(String key, String value, String... values) {
        formParams.add(key, value, values);
        return myself();
    }

    @Override
    public String getParamCharset() {
        return formParamCharset;
    }

    @Override
    public UpLoadRequest setParamCharset(String paramCharset) {
        this.formParamCharset = paramCharset;
        return myself();
    }

    @Override
    public UpLoadRequest addFormFile(FormFile... formFiles) {
        this.formFiles.addAll(Arrays.asList(formFiles));
        return myself();
    }
}
