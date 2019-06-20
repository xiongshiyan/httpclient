package top.jfunc.common.http.request;

import top.jfunc.common.http.holder.ParamHolder;
import top.jfunc.common.utils.MultiValueMap;

/**
 * Form key-value的公共处理
 * @see FormRequest
 * @see UploadRequest
 * @author xiongshiyan
 */
public interface ParamRequest {
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
    default ParamRequest addFormParam(String key, String value, String... values){
        formParamHolder().addParam(key, value, values);
        return this;
    }

    /**
     * 提供便捷设置编码的方法
     * @param paramCharset 参数编码
     * @return this
     */
    default ParamRequest setParamCharset(String paramCharset){
        formParamHolder().setParamCharset(paramCharset);
        return this;
    }

    /**
     * 接管Form param的处理
     * @return ParamHolder must not null
     */
    ParamHolder formParamHolder();
}
