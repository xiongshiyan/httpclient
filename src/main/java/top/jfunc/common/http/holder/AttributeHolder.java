package top.jfunc.common.http.holder;

import top.jfunc.common.http.request.HttpRequest;

import java.util.Map;

/**
 * 属性处理器
 * 可以在请求的时候设置属性，在其他的地方比如拦截器中可以获取到，便于统一处理
 * @see HttpRequest
 * @see top.jfunc.common.http.interceptor.Interceptor
 * @author xiongshiyan at 2019/6/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface AttributeHolder extends Holder{
    /**
     * 获取属性
     * @return 属性参数
     */
    Map<String, Object> getAttributes();

    /**
     * 批次设置属性
     * @param attributes 属性参数
     * @return this
     */
    AttributeHolder setAttributes(Map<String, Object> attributes);

    /**
     * 添加属性
     * @param key key
     * @param value value
     * @return this
     */
    AttributeHolder addAttribute(String key, Object value);

    /**
     * 去除属性
     * @param key 属性key
     * @return 被去除的属性
     */
    Object removeAttribute(String key);
}
