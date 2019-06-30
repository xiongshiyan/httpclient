package top.jfunc.common.http.holder;

import top.jfunc.common.utils.MultiValueMap;

import java.net.URL;
import java.util.Map;

/**
 * 简单的处理url的路径参数、Query参数的
 * 可能返回的是完整url路径，也可能是返回的/xxx/yyy这样的路径
 * @author xiongshiyan at 2019/6/28 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface UrlHolder {
    /**
     * 请求的URL
     * @return 请求的URL
     */
    String getUrl();

    /**
     * 重新计算全url，用于在计算了url了之后再调用可能引起更改的方法
     * @return this
     */
    String recalculateUrl();

    /**
     * 设置URL
     * @param url url
     * @return this
     */
    UrlHolder setUrl(String url);

    /**
     * 设置URL
     * @param url URL
     * @return this
     */
    default UrlHolder setUrl(URL url){
        return setUrl(url.toString());
    }

    /**
     *获取到 {@link RouteParamHolder} 可以对路径参数完全接管处理
     * @return RouteParamHolder must not be null
     */
    RouteParamHolder routeParamHolder();

    /**
     * 便捷地设置路径参数
     * @param key key
     * @param value value
     * @return this
     */
    default UrlHolder addRouteParam(String key, String value){
        routeParamHolder().put(key, value);
        return this;
    }

    /**
     * 便捷地设置路径参数
     * @param routeParams 多个路径参数
     * @return this
     */
    default UrlHolder setRouteParams(Map<String, String> routeParams){
        routeParamHolder().setMap(routeParams);
        return this;
    }

    /**
     * 获取到 {@link ParamHolder} 可以对Query参数完全接管处理
     * @return ParamHolder must not be null
     */
    ParamHolder queryParamHolder();

    /**
     * 提供便捷的设置Query参数的方法
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    default UrlHolder addQueryParam(String key, String value, String... values){
        queryParamHolder().addParam(key, value, values);
        return this;
    }

    /**
     * 提供便捷的设置Query参数的方法
     * @param queryParams 多个查询参数
     * @return this
     */
    default UrlHolder setQueryParams(MultiValueMap<String, String> queryParams){
        queryParamHolder().setParams(queryParams);
        return this;
    }

    /**
     * 提供便捷的设置Query参数的方法
     * @param queryParams 多个查询参数
     * @return this
     */
    default UrlHolder setQueryParams(Map<String, String> queryParams){
        queryParamHolder().setParams(queryParams);
        return this;
    }
}
