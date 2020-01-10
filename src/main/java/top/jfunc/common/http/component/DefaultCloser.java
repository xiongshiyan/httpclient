package top.jfunc.common.http.component;

import top.jfunc.common.utils.IoUtil;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭执行器
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultCloser implements Closer{
    @Override
    public void close(Closeable closeable) throws IOException{
        IoUtil.close(closeable);
    }
}
