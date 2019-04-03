package top.jfunc.common.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2019/4/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class IoUtil {
    private IoUtil(){}
    /**
     * 获取一个空的，防止空指针
     */
    public static InputStream emptyInputStream() {
        return new ByteArrayInputStream(new byte[]{});
    }
}
