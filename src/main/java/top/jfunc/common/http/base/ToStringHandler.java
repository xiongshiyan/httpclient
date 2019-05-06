package top.jfunc.common.http.base;

/**
 * @author xiongshiyan at 2019/5/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@FunctionalInterface
public interface ToStringHandler<T> {
    /**
     * 可能是基于Json、XML或者自定义的将一个对象转换为String
     * @param o Java对象 Bean、Map、List等
     * @return String
     */
    String toString(T o);
}
