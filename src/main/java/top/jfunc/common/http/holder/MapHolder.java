package top.jfunc.common.http.holder;

import java.util.Map;

/**
 * 对{@link Map}的一个封装，方便处理键值对
 * @see RouteParamHolder
 * @see OverwriteHeaderHolder
 * @author xiongshiyan at 2019/6/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface MapHolder extends Holder{
    /**
     * Map参数
     * @return Map参数
     */
    Map<String, String> getMap();

    /**
     * 设置Map
     * @param map map
     * @return this
     */
    MapHolder setMap(Map<String, String> map);

    /**
     * 添加参数
     * @param key key
     * @param value value
     * @return this
     */
    MapHolder put(String key, String value);
}
