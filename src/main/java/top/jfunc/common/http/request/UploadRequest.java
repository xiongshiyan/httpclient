package top.jfunc.common.http.request;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.holder.FormFileHolder;
import top.jfunc.common.http.holder.ParamHolder;
import top.jfunc.common.utils.MultiValueMap;

/**
 * 文件上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface UploadRequest<T extends UploadRequest> extends HttpRequest<T> {
    /**
     * Form参数
     * @return Form参数
     */
    default MultiValueMap<String, String> getFormParams(){
        return formParamHolder().getParams();
    }

    /**
     * 新增form参数
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    default T addFormParam(String key, String value, String... values){
        formParamHolder().addParam(key, value, values);
        return myself();
    }

    /**
     * 接管Form param的处理
     * @return ParamHolder must not null
     */
    ParamHolder formParamHolder();

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
    default T addFormFile(FormFile... formFiles){
        formFileHolder().addFormFile(formFiles);
        return myself();
    }

    /**
     * 接管文件上传信息
     * @return FormFileHolder must not be null
     */
    FormFileHolder formFileHolder();
}
