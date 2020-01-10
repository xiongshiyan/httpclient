package top.jfunc.common.http.component;

import top.jfunc.common.http.request.HttpRequest;

/**
 * 对创建出来的组件进行自定义配置
 * @author xiongshiyan at 2020/1/9 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface RequesterCustomizer<C> {
    /**
     * 对组件进行自定义处理
     * @param c 被处理的
     * @param httpRequest HttpRequest
     */
    void customize(C c, HttpRequest httpRequest);
}
