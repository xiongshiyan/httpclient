package top.jfunc.common.http.component;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭执行器
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface Closer {
    /**
     * 关闭需要关闭的
     * @param closeable 需要被关闭的
     * @throws IOException IOException
     */
    void close(Closeable closeable) throws IOException;
}
