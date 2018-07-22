package cn.zytx.common.http.converter.json;

import cn.zytx.common.json.JsonArray;
import cn.zytx.common.json.JsonObject;

/**
 * @author xiongshiyan at 2018/7/16
 */
public interface ResultConverter{
    /**
     * 将字符串转换为Bean
     * @param clazz Bean
     * @param <T> Bean
     * @return Bean
     */
    <T> T toBean(Class<T> clazz);
    /**
     * 将字符串转换为JsonObject
     * @return JsonObject
     */
    JsonObject toJsonObject();
    /**
     * 将字符串转换为JsonArray
     * @return JsonArray
     */
    JsonArray toJsonArray();
}
