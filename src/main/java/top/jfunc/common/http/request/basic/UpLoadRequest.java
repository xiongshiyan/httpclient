package top.jfunc.common.http.request.basic;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 多文件、参数同时支持的上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class UpLoadRequest extends BaseHttpRequest<UpLoadRequest> implements UploadRequest {
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
    public Iterable<FormFile> getFormFiles() {
        return this.formFiles;
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

    @Override
    public UploadRequest addFormFiles(Iterable<FormFile> formFiles) {
        Objects.requireNonNull(formFiles);
        formFiles.forEach(this.formFiles::add);
        return null;
    }
}
