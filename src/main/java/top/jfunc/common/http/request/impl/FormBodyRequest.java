package top.jfunc.common.http.request.impl;

import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.ParamUtil;
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
    public String getBody() {
        String bodyCharset = formParamHolder.getParamCharset();
        //没有显式设置就设置默认的
        if(null == getContentType()){
            setContentType(MediaType.APPLICATIPON_FORM_DATA.withCharset(bodyCharset));
        }
        return ParamUtil.contactMap(formParamHolder.getParams(), bodyCharset);
    }

    @Override
    public ParamHolder formParamHolder() {
        return formParamHolder;
    }
}
