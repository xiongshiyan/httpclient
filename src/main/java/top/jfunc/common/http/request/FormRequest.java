package top.jfunc.common.http.request;

import top.jfunc.common.http.holder.ParamHolder;
import top.jfunc.common.utils.MultiValueMap;

/**
 * 文件上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface FormRequest<T extends FormRequest> extends StringBodyRequest<T> {
    /**
     * Form参数
     * @return Form参数
     */
    default MultiValueMap<String, String> getFormParams(){
        return formParamHolder().getParams();
    }

    /**
     * 新增form参数的便捷方法
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
     * 提供便捷设置编码的方法
     * @param paramCharset 参数编码
     * @return this
     */
    default T setParamCharset(String paramCharset){
        formParamHolder().setParamCharset(paramCharset);
        return myself();
    }

    /**
     * 获取请求体编码
     * @return charset
     */
    @Override
    default String getBodyCharset() {
        return formParamHolder().getParamCharset();
    }
    /**
     * 接管Form param的处理
     * @return ParamHolder must not null
     */
    ParamHolder formParamHolder();
}
