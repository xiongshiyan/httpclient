package top.jfunc.common.http.base.handler;

/**
 * 类泛型，可以针对某个类特殊处理
 * @see FromString
 * @author xiongshiyan at 2019/5/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@FunctionalInterface
public interface FromStringHandler <T> {
    /**
     * 可能是基于Json、XML或者自定义的将String转换为Java对象
     * @param src 字符串
     * @param toClass 转换成的class
     * @return T
     */
    T as(String src, Class<T> toClass);
}
