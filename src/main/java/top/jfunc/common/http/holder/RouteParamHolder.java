package top.jfunc.common.http.holder;

/**
 * 路径参数处理器
 * @author xiongshiyan at 2019/6/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface RouteParamHolder extends MapHolder<String , String> {
    /**
     * 添加路径参数
     * @param key key
     * @param value value
     * @return this
     */
    @Override
    RouteParamHolder put(String key, String value);

    /**
     * 添加路径参数
     * @param key key
     * @param value value
     * @return this
     */
    default RouteParamHolder addRouteParam(String key, String value){
        put(key, value);
        return this;
    }

    /**
     * 添加路径参数
     * @param kvs 路径参数的k和v通过某个分隔符连起来:  "id:1" , "age:12"
     * @return this
     */
    RouteParamHolder addRouteParams(String... kvs);

    /**
     * 添加路径参数，路径参数格式为{1}/{2}/{3}的格式，默认从1开始
     * 1->first
     * 2->others[0]
     * 3->others[1]
     * ....
     * @param first 第一个
     * @param others 其他的
     * @return this
     */
    default RouteParamHolder addOrderedRouteParams(String first, String... others){
        int from = orderedParamsFrom();

        put("" + from , first);
        for (int i = 0; i < others.length; i++) {
            put(String.valueOf(from + 1 +i) , others[i]);
        }
        return this;
    }

    /**
     * 路径参数默认从1开始，如果从0开始就返回 0
     * @return 从数字几开始
     */
    default int orderedParamsFrom(){
        return 1;
    }
}
