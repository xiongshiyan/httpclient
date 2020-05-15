package top.jfunc.common.http.holder;

import top.jfunc.common.http.kv.MultiValueEntry;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.util.Map;

/**
 * wrap of {@link MultiValueMap} and impl HeaderHolder
 * @see MultiValueMap
 * @author xiongshiyan
 */
public class DefaultValuesMapHolder implements ValuesMapHolder {
    /**
     * 懒加载
     */
    private MultiValueMap<String,String> multiValueMap;

    @Override
    public MultiValueMap<String, String> get() {
        return multiValueMap;
    }

    @Override
    public ValuesMapHolder set(MultiValueMap<String, String> multiValueMap) {
        this.multiValueMap = multiValueMap;
        return this;
    }

    @Override
    public ValuesMapHolder set(Map<String, String> map) {
        if(MapUtil.notEmpty(map)){
            this.multiValueMap = ArrayListMultiValueMap.fromMap(map);
        }
        return this;
    }

    @Override
    public ValuesMapHolder set(String key, String value) {
        initMultiValueMap();
        this.multiValueMap.set(key, value);
        return this;
    }

    @Override
    public ValuesMapHolder add(String key, String value){
        initMultiValueMap();
        this.multiValueMap.add(key, value);
        return this;
    }
    @Override
    public ValuesMapHolder add(String key, String value , String... values){
        initMultiValueMap();
        this.multiValueMap.add(key , value);
        for (String val : values) {
            this.multiValueMap.add(key , val);
        }
        return this;
    }
    @Override
    public ValuesMapHolder add(String key, Iterable<String> values){
        initMultiValueMap();
        for (String value : values) {
            this.multiValueMap.add(key , value);
        }
        return this;
    }
    @Override
    public ValuesMapHolder add(MultiValueEntry multiValueEntry , MultiValueEntry... multiValueEntries){
        add(multiValueEntry.getKey() , multiValueEntry.getValue());
        for (MultiValueEntry h : multiValueEntries) {
            add(h.getKey() , h.getValue());
        }
        return this;
    }
    @Override
    public ValuesMapHolder add(Iterable<MultiValueEntry> multiValueEntries){
        for (MultiValueEntry multiValueEntry : multiValueEntries) {
            add(multiValueEntry.getKey() , multiValueEntry.getValue());
        }
        return this;
    }
    @Override
    public ValuesMapHolder add(Map.Entry<String , Iterable<String>> entry , Map.Entry<String , Iterable<String>>... entries){
        add(entry.getKey() , entry.getValue());
        for (Map.Entry<String , Iterable<String>> h : entries) {
            add(h.getKey() , h.getValue());
        }
        return this;
    }
    private void initMultiValueMap(){
        if(null == this.multiValueMap){
            this.multiValueMap = new ArrayListMultiValueMap<>(2);
        }
    }
}
