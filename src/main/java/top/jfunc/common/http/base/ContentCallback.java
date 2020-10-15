package top.jfunc.common.http.base;

import java.io.IOException;

/**
 * 写入body数据，属于一个回调方法
 * @author xiongshiyan at 2018/6/7
 * @param <CC> 处理的泛型，不同的实现方式有不同的写入方式
 */
@FunctionalInterface
public interface ContentCallback<CC> {
    /**
     * 写入数据
     * @param cc 连接
     * @throws IOException IOException
     */
    void doWriteWith(CC cc) throws IOException;
}
