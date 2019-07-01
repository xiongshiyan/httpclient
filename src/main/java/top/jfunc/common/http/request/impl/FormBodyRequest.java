package top.jfunc.common.http.request.impl;

import top.jfunc.common.http.holder.DefaultParamHolder;
import top.jfunc.common.http.holder.ParamHolder;
import top.jfunc.common.http.request.FormRequest;

/**
 * Form表单请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class FormBodyRequest extends BaseRequest<FormBodyRequest> implements FormRequest {

    public FormBodyRequest(String url){
        super(url);
    }
    public static FormBodyRequest of(String url){
        return new FormBodyRequest(url);
    }

    /**
     * form参数
     * POST请求，会作为body存在 并且设置Content-Type为 application/xxx-form-url-encoded
     * //private MultiValueMap<String,String> formParamHolder;
     */
    private ParamHolder formParamHolder = new DefaultParamHolder();

    @Override
    public ParamHolder formParamHolder() {
        return formParamHolder;
    }

    @Override
    public FormBodyRequest addFormParam(String key, String value, String... values) {
        formParamHolder().addParam(key, value, values);
        return myself();
    }

    @Override
    public FormBodyRequest setParamCharset(String paramCharset) {
        formParamHolder().setParamCharset(paramCharset);
        return myself();
    }
}
