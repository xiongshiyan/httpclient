package top.jfunc.common.http.kv;

import java.util.Map;

/**
 * singleValue Entry
 * @author xiongshiyan at 2019/7/25 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SingleValueEntry implements Map.Entry<String , String>{
    private String key;
    private String value;

    public SingleValueEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public SingleValueEntry(String key) {
        this.key = key;
    }

    public SingleValueEntry() {
    }

    public static SingleValueEntry of(String key , String value){
        return new SingleValueEntry(key, value);
    }
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String setValue(String value) {
        String temp = this.value;
        this.value = value;
        return temp;
    }
}
