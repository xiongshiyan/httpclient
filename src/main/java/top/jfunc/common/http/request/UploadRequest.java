package top.jfunc.common.http.request;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.holder.FormFileHolder;

/**
 * 文件上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface UploadRequest extends HttpRequest, ParamRequest {
    /**
     * 新增form参数
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    @Override
    default UploadRequest addFormParam(String key, String value, String... values){
        formParamHolder().addParam(key, value, values);
        return this;
    }

    /**
     * 提供便捷设置编码的方法
     * @param paramCharset 参数编码
     * @return this
     */
    @Override
    default UploadRequest setParamCharset(String paramCharset){
        formParamHolder().setParamCharset(paramCharset);
        return this;
    }

    /**
     * 上传文件信息
     * @return 上传文件信息
     */
    default FormFile[] getFormFiles(){
        return formFileHolder().getFormFiles();
    }

    /**
     * 新增文件上传信息
     * @param formFiles 上传的文件
     * @return this
     */
    default UploadRequest addFormFile(FormFile... formFiles){
        formFileHolder().addFormFile(formFiles);
        return this;
    }

    /**
     * 接管文件上传信息
     * @return FormFileHolder must not be null
     */
    FormFileHolder formFileHolder();
}
