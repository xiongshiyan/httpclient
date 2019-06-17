package top.jfunc.common.http.holder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author xiongshiyan at 2019/6/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultRouteParamHolder implements RouteParamHolder {
    private static final String SEPERATOR = ":";

    /**
     * 路径参数，形如这种URL http://httpbin.org/book/{id}，保存id和id的值
     * @since 1.0.4
     */
    private Map<String , String> routeParams;

    private String seperator = SEPERATOR;

    public DefaultRouteParamHolder(String seperator) {
        this.seperator = seperator;
    }

    public DefaultRouteParamHolder() {
    }

    @Override
    public Map<String, String> getRouteParams() {
        return routeParams;
    }

    @Override
    public RouteParamHolder setRouteParams(Map<String, String> routeParams) {
        this.routeParams = Objects.requireNonNull(routeParams);
        return this;
    }

    @Override
    public RouteParamHolder addRouteParam(String key, String value) {
        if(null == this.routeParams){
            this.routeParams = new HashMap<>(2);
        }
        this.routeParams.put(key, value);
        return this;
    }

    @Override
    public RouteParamHolder addRouteParams(String... kvs) {
        if(null == this.routeParams){
            this.routeParams = new HashMap<>(2);
        }
        for (String kv : kvs) {
            String[] split = kv.split(seperator);
            this.routeParams.put(split[0] , split[1]);
        }
        return null;
    }

    public String getSeperator() {
        return seperator;
    }

    public void setSeperator(String seperator) {
        this.seperator = seperator;
    }
}
