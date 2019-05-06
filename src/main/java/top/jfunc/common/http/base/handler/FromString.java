package top.jfunc.common.http.base.handler;

/**
 * 方法泛型，可以针对很多类统一处理
 * @see FromStringHandler
 * @author xiongshiyan at 2019/5/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@FunctionalInterface
public interface FromString {
    /**
     * 可能是基于Json、XML或者自定义的将String转换为Java对象
     * @param src 字符串
     * @param toClass 转换成的class
     * @return T
     */
    <T> T as(String src, Class<T> toClass);
}
