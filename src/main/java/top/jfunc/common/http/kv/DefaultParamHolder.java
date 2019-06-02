package top.jfunc.common.http.kv;

import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.MultiValueMap;

import java.util.Map;
import java.util.Objects;

/**
 * wrap of {@link MultiValueMap} and impl ParamHolder
 * @see top.jfunc.common.http.kv.ParamHolder
 * @see MultiValueMap
 */
public class DefaultParamHolder implements ParamHolder{
    /**
     * 查询参数，拼装在URL后面 ?
     * @since 1.0.4(支持Query参数)/1.1.3(挪移至此更好管理)
     */
    private MultiValueMap<String,String> params;

    @Override
    public MultiValueMap<String, String> getParams() {
        return params;
    }

    @Override
    public ParamHolder setParams(MultiValueMap<String, String> queryParams) {
        this.params = Objects.requireNonNull(queryParams);
        return this;
    }

    public ParamHolder setParams(ArrayListMultimap<String, String> queryParams) {
        Objects.requireNonNull(queryParams);
        this.params = ArrayListMultiValueMap.fromMap(queryParams);
        return this;
    }
    @Override
    public ParamHolder setParams(Map<String, String> queryParams) {
        Objects.requireNonNull(queryParams);
        this.params = ArrayListMultiValueMap.fromMap(queryParams);
        return this;
    }
    @Override
    public ParamHolder addParam(String key, String value){
        initParams();
        this.params.add(key, value);
        return this;
    }
    @Override
    public ParamHolder addParam(String key, String value, String... values){
        initParams();
        this.params.add(key , value);
        for (String val : values) {
            this.params.add(key , val);
        }
        return this;
    }
    @Override
    public ParamHolder addParam(String key, Iterable<String> values){
        initParams();
        for (String value : values) {
            this.params.add(key , value);
        }
        return this;
    }
    @Override
    public ParamHolder addParam(Parameter parameter , Parameter... parameters){
        addParam(parameter.getKey() , parameter.getValue());
        for (Parameter param : parameters) {
            addParam(param.getKey() , param.getValue());
        }
        return this;
    }
    @Override
    public ParamHolder addParam(Iterable<Parameter> parameters){
        for (Map.Entry<String , Iterable<String>> parameter : parameters) {
            addParam(parameter.getKey() , parameter.getValue());
        }
        return this;
    }
    @Override
    public ParamHolder addParam(Map.Entry<String , Iterable<String>> parameter , Map.Entry<String , Iterable<String>>... parameters){
        addParam(parameter.getKey() , parameter.getValue());
        for (Map.Entry<String , Iterable<String>> param : parameters) {
            addParam(param.getKey() , param.getValue());
        }
        return this;
    }
    private void initParams(){
        if(null == this.params){
            this.params = new ArrayListMultiValueMap<>(2);
        }
    }
}
