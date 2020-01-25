package top.jfunc.common.http.interfacing;

import top.jfunc.common.http.base.Method;
import top.jfunc.common.http.request.HttpRequest;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
interface RequestFactory {
    /**
     * 生成HttpRequest请求
     * @param args 参数
     * @return Implementation of HttpRequest
     */
    HttpRequest httpRequest(Object[] args);

    /**
     * 请求方法
     */
    Method getHttpMethod();
}
