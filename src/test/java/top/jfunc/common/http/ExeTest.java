package top.jfunc.common.http;

import org.junit.Test;
import top.jfunc.common.http.exe.apache.ApacheExeSmartHttpClient;
import top.jfunc.common.http.exe.jdk.NativeExeSmartHttpClient;
import top.jfunc.common.http.exe.jodd.JoddExeSmartHttpClient;
import top.jfunc.common.http.exe.okhttp3.OkHttp3ExeSmartHttpClient;
import top.jfunc.common.http.smart.Request;
import top.jfunc.common.http.smart.SmartHttpClient;

import java.io.IOException;

public class ExeTest {
    @Test
    public void testExeOkHttp3() throws IOException {
        SmartHttpClient smartHttpClient = new OkHttp3ExeSmartHttpClient();
        test(smartHttpClient);
    }
    @Test
    public void testExeJodd() throws IOException {
        SmartHttpClient smartHttpClient = new JoddExeSmartHttpClient();
        test(smartHttpClient);
    }
    @Test
    public void testExeJdk() throws IOException {
        SmartHttpClient smartHttpClient = new NativeExeSmartHttpClient();
        test(smartHttpClient);
    }
    @Test
    public void testExeApache() throws IOException {
        SmartHttpClient smartHttpClient = new ApacheExeSmartHttpClient();
        test(smartHttpClient);
    }

    private void test(SmartHttpClient smartHttpClient) throws IOException {
        String s = smartHttpClient.get("https://www.baidu.com");
        System.out.println(s);
        System.out.println(smartHttpClient.head(Request.of("https://www.baidu.com")));
    }
}
