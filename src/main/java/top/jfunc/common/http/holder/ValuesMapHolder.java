package top.jfunc.common.http.holder;

import top.jfunc.common.http.kv.MultiValueEntry;
import top.jfunc.common.utils.MultiValueMap;

import java.util.Map;

/**
 * 多值处理器，对于Query、Header、Form参数可能存在k-v1、v2的情况
 * @author xiongshiyan
 */
public interface ValuesMapHolder {
    /**
     * 请求的多值参数
     * @return MultiValueMap maybe Nullable
     */
    MultiValueMap<String, String> get();

    /**
     * 设置MultiValueMap
     * @param multiValueMap 多值参数
     * @return this
     */
    ValuesMapHolder set(MultiValueMap<String, String> multiValueMap);

    /**
     * 设置Map
     * @param map 单值参数兼容
     * @return this
     */
    ValuesMapHolder set(Map<String, String> map);

    /**
     * 添加,set方式
     * @param key key
     * @param value value
     * @return this
     */
    ValuesMapHolder set(String key, String value);

    /**
     * set多个kv
     * @param entry kv
     * @param entries kvs
     * @return this
     */
    default ValuesMapHolder set(Map.Entry<String , String> entry, Map.Entry<String , String>... entries){
        set(entry.getKey() , entry.getValue());
        for (Map.Entry<String , String> kv : entries) {
            set(kv.getKey() , kv.getValue());
        }
        return this;
    }

    /**
     * set多个kv
     * @param entries kvs
     * @return this
     */
    default ValuesMapHolder set(Iterable<Map.Entry<String , String>> entries){
        entries.forEach(this::set);
        return this;
    }




    /**
     * 添加,add方式
     * @param key key
     * @param value value
     * @return this
     */
    ValuesMapHolder add(String key, String value);

    /**
     * 添加
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    ValuesMapHolder add(String key, String value, String... values);

    /**
     * 添加
     * @param key key
     * @param values values
     * @return this
     */
    ValuesMapHolder add(String key, Iterable<String> values);

    /**
     * 添加MultiValueEntry
     * @param multiValueEntry MultiValueEntry
     * @param multiValueEntries MultiValueEntry
     * @return this
     */
    ValuesMapHolder add(MultiValueEntry multiValueEntry, MultiValueEntry... multiValueEntries);

    /**
     * 添加Iterable<MultiValueEntry>
     * @param multiValueEntries Iterable<MultiValueEntry>
     * @return this
     */
    ValuesMapHolder add(Iterable<MultiValueEntry> multiValueEntries);

    /**
     * 添加Map.Entry<String, Iterable<String>>
     * @param entry Map.Entry
     * @param entries Map.Entry
     * @return this
     */
    ValuesMapHolder add(Map.Entry<String, Iterable<String>> entry, Map.Entry<String, Iterable<String>>... entries);
}
