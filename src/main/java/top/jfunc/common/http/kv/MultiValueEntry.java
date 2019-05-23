package top.jfunc.common.http.kv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 实现通用的 Map.Entry
 * @see Header
 * @see Parameter
 * @author xiongshiyan at 2019/5/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class MultiValueEntry implements Map.Entry<String , Iterable<String>>{
    private String key;
    private Iterable<String> value;

    public MultiValueEntry(String key, String value , String... values) {
        this.key = key;
        List<String> temp = new ArrayList<>(values.length + 1);
        temp.add(value);
        temp.addAll(Arrays.asList(values));
        this.value = temp;
    }
    public MultiValueEntry(String key, Iterable<String> values) {
        this.key = key;
        this.value = values;
    }
    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public Iterable<String> getValue() {
        return value;
    }

    @Override
    public Iterable<String> setValue(Iterable<String> value) {
        return this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiValueEntry that = (MultiValueEntry) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MultiValueEntry{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
