package top.jfunc.common.http.interfacing;

import java.io.IOException;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
interface ServiceMethod<T> {
    /**
     * 传参调用返回
     * @param args 参数:接口里面的真正传递的参数
     * @return T 接口的返回值
     * @throws IOException IOException
     */
    T invoke(Object[] args) throws IOException;
}
