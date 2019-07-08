package top.jfunc.common.http.holder;

import top.jfunc.common.http.kv.Header;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.MultiValueMap;

import java.util.Map;

/**
 * 请求头处理器，新增、获取header
 * @author xiongshiyan
 */
public interface HeaderHolder{
    /**
     * 请求的Header
     * @return 请求的Header
     */
    MultiValueMap<String, String> getHeaders();

    /**
     * 有些请求可能经过一些处理之后需要改变header重新设置回去
     * @param headers 处理过后的header
     * @return this
     */
    HeaderHolder setHeaders(MultiValueMap<String, String> headers);

    /**
     * 设置请求的header
     * @param headers headers
     * @return this
     */
    HeaderHolder setHeaders(ArrayListMultimap<String, String> headers);

    /**
     * 设置header
     * @param headers headerHolder
     * @return this
     */
    HeaderHolder setHeaders(Map<String, String> headers);

    /**
     * 添加header
     * @param key key
     * @param value value
     * @return this
     */
    HeaderHolder addHeader(String key, String value);

    /**
     * 添加header
     * @param key key
     * @param value value
     * @param values values
     * @return this
     */
    HeaderHolder addHeader(String key, String value, String... values);

    /**
     * 添加header
     * @param key key
     * @param values values
     * @return this
     */
    HeaderHolder addHeader(String key, Iterable<String> values);

    /**
     * 添加header
     * @param header header
     * @param headers headerHolder
     * @return this
     */
    HeaderHolder addHeader(Header header, Header... headers);

    /**
     * 添加header
     * @param headers headerHolder
     * @return this
     */
    HeaderHolder addHeader(Iterable<Header> headers);

    /**
     * 添加header
     * @param header header
     * @param headers headerHolder
     * @return this
     */
    HeaderHolder addHeader(Map.Entry<String, Iterable<String>> header, Map.Entry<String, Iterable<String>>... headers);
}
