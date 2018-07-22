package cn.zytx.common.http;

import cn.zytx.common.http.smart.SmartHttpClient;
import org.junit.Test;

public class DelegateTest {
    @Test
    public void testDelegate(){
        SmartHttpClient delegate = HttpUtil.delegate();
        System.out.println(delegate);
    }
}
