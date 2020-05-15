package top.jfunc.common.http;

import org.junit.Test;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.smart.*;

import java.io.IOException;

/**
 * @author xiongshiyan at 2019/4/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ConfigSettingTest {
    @Test
    public void testGetOkHttp3(){
        SmartHttpClient http = new OkHttp3SmartHttpClient();
        testGet(http);
    }
    @Test
    public void testGetApacheHttp(){
        ApacheSmartHttpClient http = new ApacheSmartHttpClient();
        testGet(http);
    }
    @Test
    public void testGetNativeHttp(){
        NativeSmartHttpClient http = new NativeSmartHttpClient();
        testGet(http);
    }
    @Test
    public void testGetJoddHttp(){
        JoddSmartHttpClient http = new JoddSmartHttpClient();
        testGet(http);
    }
    private void testGet(SmartHttpClient http){

        Config config = Config.defaultConfig()
                .setBaseUrl("https://fanyi.baidu.com/")
                .setDefaultBodyCharset("UTF-8")
                .setDefaultResultCharset("UTF-8")
                .setDefaultConnectionTimeout(15000)
                .setDefaultReadTimeout(15000);
        config.headerHolder().add("xx" , "xx");
        http.setConfig(config);

        String url = "?aldtype=85#zh/en/%E5%AE%8C%E6%95%B4%E7%9A%84%E6%88%91";
        try {
            Request request = Request.of(url).setResultCharset("UTF-8");
            request.headerHolder().add("saleType" , "2");
            Response response = http.get(request);
            System.out.println(response);
            System.out.println(response.getHeaders());

            String s = http.get(url);
            System.out.println(s);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
