package top.jfunc.common.http.request.basic;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.request.FormRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.util.Map;

/**
 * Form表单请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class FormBodyRequest extends BaseHttpRequest<FormBodyRequest> implements FormRequest {

    public FormBodyRequest(String url){
        super(url);
    }
    public static FormBodyRequest of(String url){
        return new FormBodyRequest(url);
    }

    private MultiValueMap<String , String> formParams = new ArrayListMultiValueMap<>(2);
    private String formParamCharset = HttpConstants.DEFAULT_CHARSET;

    @Override
    public FormBodyRequest setFormParams(Map<String, String> params) {
        if(MapUtil.notEmpty(params)){
            this.formParams = ArrayListMultiValueMap.fromMap(params);
        }
        return myself();
    }
    @Override
    public FormBodyRequest setFormParams(MultiValueMap<String, String> params) {
        this.formParams = params;
        return myself();
    }

    @Override
    public FormBodyRequest addFormParam(String key, String value, String... values) {
        formParams.add(key, value, values);
        return myself();
    }

    @Override
    public MultiValueMap<String, String> getFormParams() {
        return formParams;
    }

    @Override
    public String getBodyCharset() {
        return formParamCharset;
    }

    @Override
    public FormBodyRequest setBodyCharset(String bodyCharset) {
        this.formParamCharset = bodyCharset;
        return myself();
    }
}
