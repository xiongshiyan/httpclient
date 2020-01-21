package top.jfunc.common.http.component;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/21 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CloseNoneCloser implements Closer {
    @Override
    public void close(Closeable closeable) throws IOException {
        // DO NOTHING TO CLOSE NONE
    }
}
