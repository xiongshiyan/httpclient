package top.jfunc.common.http.base;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2019/7/9 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class StreamUtil {
    public static InputStream emptyInputStream() {
        return new ByteArrayInputStream(new byte[]{});
    }
}
