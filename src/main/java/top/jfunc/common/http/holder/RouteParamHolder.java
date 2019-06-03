package top.jfunc.common.http.holder;

import java.util.Map;

/**
 * 路径参数处理器
 * @author xiongshiyan at 2019/6/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface RouteParamHolder {
    /**
     * 路径参数
     * @return 路径参数
     */
    Map<String, String> getRouteParams();

    /**
     * 设置路径参数
     * @param routeParams 路径参数映射
     * @return this
     */
    RouteParamHolder setRouteParams(Map<String, String> routeParams);

    /**
     * 添加路径参数
     * @param key key
     * @param value value
     */
    RouteParamHolder addRouteParam(String key, String value);

    /**
     * 添加路径参数
     * @param kvs 路径参数的k和v通过某个分隔符连起来:  "id:1" , "age:12"
     * @return this
     */
    RouteParamHolder addRouteParams(String... kvs);
}
