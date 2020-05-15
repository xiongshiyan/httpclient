package top.jfunc.common.http.holder;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.utils.MultiValueMap;

/**
 * wrap of {@link MultiValueMap} and impl ParamHolder
 * @see ParamHolder
 * @see MultiValueMap
 * @author xiongshiyan
 */
public class DefaultParamHolder extends DefaultValuesMapHolder implements ParamHolder{
    /**
     * 参数编码
     * @since 1.1.4
     */
    private String paramCharset = Config.DEFAULT_CHARSET;


    @Override
    public String getParamCharset() {
        return paramCharset;
    }

    @Override
    public ParamHolder setParamCharset(String paramCharset) {
        this.paramCharset = paramCharset;
        return this;
    }
}
