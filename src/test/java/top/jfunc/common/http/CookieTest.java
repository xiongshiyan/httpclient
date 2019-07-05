package top.jfunc.common.http;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.impl.GetRequest;
import top.jfunc.common.http.smart.*;

import java.net.*;
import java.util.List;
import java.util.Map;

/**
 * @author xiongshiyan at 2019/5/20 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@Ignore
public class CookieTest {
    @Test
    public void testAddCookie() throws Exception{
        // 创建一个 CookieManager对象
        CookieManager manager = new CookieManager();
        // 接受所有的Cookie
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        // 保存这个定制的CookieManager
        CookieHandler.setDefault(manager);
        //CookieStore cookieJar = manager.getCookieStore();
        // 用List获取cookie，因为cookie中可能包含多个信息
        //List<HttpCookie> cookies = cookieJar.getCookies();


        //请求的header：从cookiejar中获取，这些写入header ： requestheaders不为空即可
        Map<String, List<String>> stringListMap = manager.get(new URL("").toURI(), null);
        /**其他的操作省略*/
        //从返回的header中获取cookie保存 responseheader就是返回的header
        manager.put(new URL("").toURI() , null);

        //写入request

        /*cookies.forEach(httpCookie -> {
            String cookie = httpCookie.toString();
            System.out.println("Cookie:" + cookie);
        });
*/
        // 这里我们就获取到了cookie，将其返回。

        //return cookies;
    }


    private CookieManager manager;

    @Before
    public void init(){
        // 创建一个 CookieManager对象
        manager = new CookieManager();
        // 接受所有的Cookie
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        // 保存这个定制的CookieManager
        //可调可不调
        // CookieHandler.setDefault(manager);
    }

    @Test
    public void testCookieOkHttp3()throws Exception{
        //只要设置了CookieHandler就支持Cookie
        SmartHttpClient smartHttpClient = new OkHttp3SmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieHandler(manager));
        testCookie(smartHttpClient);
    }
    @Test
    public void testCookieApacheHttp()throws Exception{
        //只要设置了CookieHandler就支持Cookie
        SmartHttpClient smartHttpClient = new ApacheSmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieHandler(manager));
        testCookie(smartHttpClient);
    }
    @Test
    public void testCookieNativeHttp()throws Exception{
        //只要设置了CookieHandler就支持Cookie
        SmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieHandler(manager));
        testCookie(smartHttpClient);
    }
    @Test
    public void testCookieJoddHttp() throws Exception{
        //只要设置了CookieHandler就支持Cookie
        SmartHttpClient smartHttpClient = new JoddSmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieHandler(manager));
        testCookie(smartHttpClient);
    }

    private void testCookie(SmartHttpClient smartHttpClient) throws Exception{
        //服务端设置Cookie
        Response response = smartHttpClient.get(GetRequest.of("http://localhost:8080/http-server-test/cookie/getCookie"));
        System.out.println(response.getHeaders());
        //客户端自动带上Cookie
        Response response1 = smartHttpClient.get(GetRequest.of("http://localhost:8080/http-server-test/cookie/setCookie"));
        System.out.println(response1.getBody());
    }
}
