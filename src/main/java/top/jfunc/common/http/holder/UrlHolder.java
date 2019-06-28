package top.jfunc.common.http.holder;

import top.jfunc.common.http.Protocol;
import top.jfunc.common.utils.MultiValueMap;

import java.net.URL;
import java.util.Map;

/**
 * 处理URL参数的：包括 protocol、host、port、path、routeParam、queryParam
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
    String recalculate();

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
     * 设置协议
     * @param protocol 协议
     * @return this
     */
    UrlHolder protocol(Protocol protocol);

    /**
     * 获取协议
     * @return 协议
     */
    Protocol protocol();

    /**
     * 设置host，可能是IP、hostName
     * @param host host
     * @return this
     */
    UrlHolder host(String host);

    /**
     * 获取主机
     * @return 主机
     */
    String host();

    /**
     * 设置端口
     * @param port port
     * @return this
     */
    UrlHolder port(int port);

    /**
     * 获取端口
     * @return 端口
     */
    int port();


    /**
     * 一起设置协议和host
     * @param protocol 协议
     * @param host host
     * @return this
     */
    default UrlHolder protocolHost(Protocol protocol, String host){
        protocol(protocol);
        host(host);
        return this;
    }

    /**
     * 一起设置协议和端口
     * @param protocol 协议
     * @param port 端口
     * @return this
     */
    default UrlHolder protocolPort(Protocol protocol, int port){
        protocol(protocol);
        port(port);
        return this;
    }
    /**
     * 一起设置host和端口
     * @param host host
     * @param port 端口
     * @return this
     */
    default UrlHolder hostPort(String host, int port){
        host(host);
        port(port);
        return this;
    }

    /**
     * 三个一起设置
     * @param protocol 协议
     * @param host host
     * @param port 端口
     * @return this
     */
    default UrlHolder php(Protocol protocol, String host, int port){
        protocol(protocol);
        host(host);
        port(port);
        return this;
    }


    /**
     * 设置路径
     * @param path 路径，可以设置路径参数/{routeParam}/{routeParam}
     * @param paths 多个路径拼接在后面
     * @return this
     */
    UrlHolder path(String path, String... paths);

    /**
     * 获取路径
     * @return 路径
     */
    String path();

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
