package top.jfunc.common.http.holder;

import top.jfunc.common.http.kv.Parameter;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.MultiValueMap;

import java.util.Map;

/**
 * Query参数、Form参数处理器
 * @author xiongshiyan
 */
public interface ParamHolder {
    /**
     * Param参数
     * @return Param参数
     */
    MultiValueMap<String, String> getParams();

    /**
     * 参数编码
     * @return 参数编码
     */
    String getParamCharset();

    /**
     * 设置参数编码
     * @param charset 编码
     * @return this
     */
    ParamHolder setParamCharset(String charset);

    /**
     * 设置Param参数
     * @param params Param参数
     * @return this
     */
    ParamHolder setParams(MultiValueMap<String, String> params);

    /**
     * 设置Param参数
     * @param params Param参数
     * @return this
     */
    ParamHolder setParams(ArrayListMultimap<String, String> params);

    /**
     * 设置Param参数
     * @param params Param参数
     * @return this
     */
    ParamHolder setParams(Map<String, String> params);

    /**
     * 添加Param参数
     * @param key key
     * @param value value
     * @return this
     */
    ParamHolder addParam(String key, String value);

    /**
     * 添加Param参数
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    ParamHolder addParam(String key, String value, String... values);

    /**
     * 添加Param参数
     * @param key key
     * @param values values
     * @return this
     */
    ParamHolder addParam(String key, Iterable<String> values);

    /**
     * 添加Param参数
     * @param parameter parameter
     * @param parameters parameters
     * @return this
     */
    ParamHolder addParam(Parameter parameter, Parameter... parameters);

    /**
     * 添加Param参数
     * @param parameters parameters
     * @return this
     */
    ParamHolder addParam(Iterable<Parameter> parameters);

    /**
     * 添加Param参数
     * @param parameter parameter
     * @param parameters parameters
     * @return this
     */
    ParamHolder addParam(Map.Entry<String, Iterable<String>> parameter, Map.Entry<String, Iterable<String>>... parameters);
}
