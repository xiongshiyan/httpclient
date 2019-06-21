package top.jfunc.common.http.holder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author xiongshiyan at 2019/6/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultAttributeHolder implements AttributeHolder {
    private Map<String , Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public AttributeHolder setAttributes(Map<String, Object> attributes) {
        this.attributes = Objects.requireNonNull(attributes);
        return this;
    }

    @Override
    public AttributeHolder addAttribute(String key, Object value) {
        if(null == this.attributes){
            this.attributes = new HashMap<>(2);
        }
        this.attributes.put(key, value);
        return this;
    }

    @Override
    public Object removeAttribute(String key) {
        if(null == this.attributes){
            return null;
        }
        return this.attributes.remove(key);
    }
}
