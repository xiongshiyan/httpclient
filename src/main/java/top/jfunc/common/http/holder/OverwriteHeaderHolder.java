package top.jfunc.common.http.holder;

import java.util.Map;

/**
 * 只能存在一个key的header处理器，例如 Connection、Host等
 * @author xiongshiyan at 2019/6/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface OverwriteHeaderHolder extends MapHolder{
    /**
     * 路径参数
     * @return 路径参数
     */
    @Override
    Map<String, String> getMap();

    /**
     * 设置路径参数
     * @param map 路径参数映射
     * @return this
     */
    @Override
    MapHolder setMap(Map<String, String> map);

    /**
     * 添加路径参数
     * @param key key
     * @param value value
     * @return this
     */
    @Override
    OverwriteHeaderHolder put(String key, String value);
}
