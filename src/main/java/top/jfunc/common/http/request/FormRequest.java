package top.jfunc.common.http.request;

import top.jfunc.common.http.kv.ParamHolder;
import top.jfunc.common.utils.MultiValueMap;

/**
 * 文件上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface FormRequest extends StringBodyRequest {
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
    //FormRequest addFormParam(String key, String value, String... values);

    /**
     * 接管Form param的处理
     * @return ParamHolder must not null
     */
    ParamHolder formParamHolder();
}
