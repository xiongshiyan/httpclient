package top.jfunc.common.http.holderrequest;

import top.jfunc.common.http.base.MediaType;
import top.jfunc.common.http.util.ParamUtil;
import top.jfunc.common.http.holder.ParamHolder;
import top.jfunc.common.http.request.FormRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.util.Map;

/**
 * 文件上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface HolderFormRequest extends HolderStringBodyRequest, FormRequest {
    /**
     * 接管Form param的处理
     * @return ParamHolder must not null
     */
    ParamHolder formParamHolder();

    /**
     * Form参数
     * @return Form参数
     */
    @Override
    default MultiValueMap<String, String> getFormParams(){
        return formParamHolder().getParams();
    }

    /**
     * 设置Form参数
     * @param params Form参数
     * @return this
     */
    @Override
    default HolderFormRequest setFormParams(Map<String, String> params){
        formParamHolder().setParams(params);
        return this;
    }
    /**
     * 设置Form参数
     * @param params Form参数
     * @return this
     */
    @Override
    default HolderFormRequest setFormParams(MultiValueMap<String, String> params){
        formParamHolder().setParams(params);
        return this;
    }

    /**
     * 新增form参数的便捷方法
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    @Override
    default HolderFormRequest addFormParam(String key, String value, String... values){
        formParamHolder().addParam(key, value, values);
        return this;
    }

    /**
     * 提供便捷设置编码的方法
     * @param paramCharset 参数编码
     * @return this
     */
    default HolderFormRequest setParamCharset(String paramCharset){
        formParamHolder().setParamCharset(paramCharset);
        return this;
    }
    /**
     * 提供便捷设置编码的方法
     * @param bodyCharset 参数编码
     * @return this
     */
    @Override
    default HolderFormRequest setBodyCharset(String bodyCharset){
        setParamCharset(bodyCharset);
        return this;
    }

    /**
     * form参数生成body
     * @see ParamUtil#contactMap(MultiValueMap)
     * @return body
     */
    @Override
    default String getBody() {
        ParamHolder formParamHolder = formParamHolder();
        String bodyCharset = getConfig().calculateBodyCharset(formParamHolder.getParamCharset(), getContentType());
        //没有显式设置就设置默认的
        if(null == getContentType()){
            setContentType(MediaType.APPLICATIPON_FORM_DATA.withCharset(bodyCharset));
        }
        return ParamUtil.contactMap(formParamHolder.getParams(), bodyCharset);
    }
    /**
     * 获取请求体编码
     * @return charset
     */
    @Override
    default String getBodyCharset() {
        return formParamHolder().getParamCharset();
    }
}
