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
    private void testGet(SmartHttpClient http){

        http.setConfig(Config.defaultConfig()
                        .setBaseUrl("https://fanyi.baidu.com/")
                        .addDefaultHeader("xx" , "xx")
                        .setDefaultBodyCharset("UTF-8")
                        .setDefaultResultCharset("UTF-8")
                        .setDefaultConnectionTimeout(15000)
                        .setDefaultReadTimeout(15000)

                //.....
        );

        String url = "?aldtype=85#zh/en/%E5%AE%8C%E6%95%B4%E7%9A%84%E6%88%91";
        try {
            Response response = http.get(Request.of(url).addHeader("saleType" , "2").setResultCharset("UTF-8"));
            System.out.println(response);
            System.out.println(response.getHeaders());

            String s = http.get(url);
            System.out.println(s);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
