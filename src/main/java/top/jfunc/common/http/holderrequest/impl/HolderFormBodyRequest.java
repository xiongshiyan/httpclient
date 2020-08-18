package top.jfunc.common.http.holderrequest.impl;

import top.jfunc.common.http.holder.DefaultParamHolder;
import top.jfunc.common.http.holder.ParamHolder;
import top.jfunc.common.http.holderrequest.HolderFormRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.net.URL;
import java.util.Map;

/**
 * Form表单请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class HolderFormBodyRequest extends BaseHolderHttpRequest<HolderFormBodyRequest> implements HolderFormRequest {

    public HolderFormBodyRequest(String url){
        super(url);
    }
    public HolderFormBodyRequest(URL url){
        super(url);
    }
    public HolderFormBodyRequest(){
    }

    public static HolderFormBodyRequest of(URL url){
        return new HolderFormBodyRequest(url);
    }
    public static HolderFormBodyRequest of(String url){
        return new HolderFormBodyRequest(url);
    }
    public static HolderFormBodyRequest of(){
        return new HolderFormBodyRequest();
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
    public HolderFormBodyRequest setFormParams(Map<String, String> params) {
        formParamHolder().set(params);
        return myself();
    }
    @Override
    public HolderFormBodyRequest setFormParams(MultiValueMap<String, String> params) {
        formParamHolder().set(params);
        return myself();
    }

    @Override
    public HolderFormBodyRequest addFormParam(String key, String value, String... values) {
        formParamHolder().add(key, value, values);
        return myself();
    }

    @Override
    public HolderFormBodyRequest setParamCharset(String paramCharset) {
        formParamHolder().setParamCharset(paramCharset);
        return myself();
    }

    public void setFormParamHolder(ParamHolder formParamHolder) {
        this.formParamHolder = formParamHolder;
    }
}
