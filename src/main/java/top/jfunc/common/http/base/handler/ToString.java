package top.jfunc.common.http.base.handler;

/**
 * 方法泛型，可以针对很多类统一处理
 * @see ToStringHandler
 * @author xiongshiyan at 2019/5/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@FunctionalInterface
public interface ToString {
    /**
     * 可能是基于Json、XML或者自定义的将一个对象转换为String
     * @param o Java对象 Bean、Map、List等
     * @return String
     */
    String toString(Object o);
}
