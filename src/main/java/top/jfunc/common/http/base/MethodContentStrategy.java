package top.jfunc.common.http.base;

/**
 * 判断一个方法{@link Method}是否能够写入BODY数据
 * @author xiongshiyan at 2020/10/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@FunctionalInterface
public interface MethodContentStrategy {
    /**
     * 方法是否支持body
     * @param method http method
     * @return true if support
     */
    boolean supportContent(Method method);
}
