package top.jfunc.common.http.smart;

import top.jfunc.common.utils.MultiValueMap;

/**
 * 从请求响应中获取header
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface HeaderExtractor<S> extends ResponseExtractor<S , MultiValueMap<String , String>> {
}
