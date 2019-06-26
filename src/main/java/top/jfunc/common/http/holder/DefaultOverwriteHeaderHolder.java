package top.jfunc.common.http.holder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author xiongshiyan at 2019/6/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOverwriteHeaderHolder implements OverwriteHeaderHolder {
    private Map<String , String> map;

    @Override
    public Map<String, String> getMap() {
        return map;
    }

    @Override
    public OverwriteHeaderHolder setMap(Map<String, String> map) {
        this.map = Objects.requireNonNull(map);
        return this;
    }

    @Override
    public OverwriteHeaderHolder put(String key, String value) {
        if(null == this.map){
            this.map = new HashMap<>(2);
        }
        this.map.put(key, value);
        return this;
    }
}
