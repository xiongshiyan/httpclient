package top.jfunc.common.http;

import top.jfunc.common.http.smart.SmartHttpClient;
import org.junit.Test;

public class DelegateTest {
    @Test
    public void testDelegate(){
        SmartHttpClient delegate = HttpUtil.delegate();
        System.out.println(delegate);
    }
}
