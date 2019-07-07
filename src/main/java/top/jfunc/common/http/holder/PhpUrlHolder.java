package top.jfunc.common.http.holder;

import top.jfunc.common.http.Protocol;
import top.jfunc.common.utils.MultiValueMap;

import java.net.URL;
import java.util.Map;

/**
 * 在{@link UrlHolder}基础上增加对各个part分别处理的：包括 protocol、host、port、path
 * 名字得于 protocol-host-port
 * @author xiongshiyan at 2019/6/28 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface PhpUrlHolder extends UrlHolder {
    /**
     * 请求的URL
     * @return 请求的URL
     */
    @Override
    String getUrl();

    /**
     * 设置URL
     * @param url url
     * @return this
     */
    @Override
    PhpUrlHolder setUrl(String url);

    /**
     * 设置URL
     * @param url URL
     * @return this
     */
    @Override
    default PhpUrlHolder setUrl(URL url){
        return setUrl(url.toString());
    }

    /**
     * 设置协议
     * @param protocol 协议
     * @return this
     */
    PhpUrlHolder protocol(Protocol protocol);

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
    PhpUrlHolder host(String host);

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
    PhpUrlHolder port(int port);

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
    default PhpUrlHolder protocolHost(Protocol protocol, String host){
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
    default PhpUrlHolder protocolPort(Protocol protocol, int port){
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
    default PhpUrlHolder hostPort(String host, int port){
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
    default PhpUrlHolder php(Protocol protocol, String host, int port){
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
    PhpUrlHolder path(String path, String... paths);

    /**
     * 获取路径
     * @return 路径
     */
    String path();

    /**
     * 便捷地设置路径参数
     * @param key key
     * @param value value
     * @return this
     */
    @Override
    default PhpUrlHolder addRouteParam(String key, String value){
        routeParamHolder().put(key, value);
        return this;
    }

    /**
     * 便捷地设置路径参数
     * @param routeParams 多个路径参数
     * @return this
     */
    @Override
    default PhpUrlHolder setRouteParams(Map<String, String> routeParams){
        routeParamHolder().setMap(routeParams);
        return this;
    }

    /**
     * 提供便捷的设置Query参数的方法
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    @Override
    default PhpUrlHolder addQueryParam(String key, String value, String... values){
        queryParamHolder().addParam(key, value, values);
        return this;
    }

    /**
     * 提供便捷的设置Query参数的方法
     * @param queryParams 多个查询参数
     * @return this
     */
    @Override
    default PhpUrlHolder setQueryParams(MultiValueMap<String, String> queryParams){
        queryParamHolder().setParams(queryParams);
        return this;
    }

    /**
     * 提供便捷的设置Query参数的方法
     * @param queryParams 多个查询参数
     * @return this
     */
    @Override
    default PhpUrlHolder setQueryParams(Map<String, String> queryParams){
        queryParamHolder().setParams(queryParams);
        return this;
    }
}
