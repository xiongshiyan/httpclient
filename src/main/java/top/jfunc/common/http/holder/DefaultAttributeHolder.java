package top.jfunc.common.http.holder;

import top.jfunc.common.utils.MapUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiongshiyan at 2019/6/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultAttributeHolder implements AttributeHolder {
    private Map<String , Object> attributes;

    @Override
    public Map<String, Object> getMap() {
        return attributes;
    }

    @Override
    public void setMap(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public AttributeHolder put(String key, Object value) {
        if(null == this.attributes){
            this.attributes = new HashMap<>(2);
        }
        this.attributes.put(key, value);
        return this;
    }

    @Override
    public Object remove(String key) {
        if(MapUtil.isEmpty(attributes)){
            return null;
        }
        return this.attributes.remove(key);
    }
}
