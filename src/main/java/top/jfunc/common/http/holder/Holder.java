package top.jfunc.common.http.holder;

/**
 * 基本的获取和设置接口
 * @author xiongshiyan at 2019/7/5 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface Holder <T>{
    /**
     * 基本的获取
     * @return 设置的什么就是什么
     */
    T get();

    /**
     * 基本的设置能力
     * @param t 要设置的
     * @return this
     */
    Holder<T> set(T t);
}
