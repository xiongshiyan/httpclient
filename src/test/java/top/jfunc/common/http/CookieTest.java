package top.jfunc.common.http;

import org.junit.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.cookie.CookieJar;
import top.jfunc.common.http.cookie.JdkCookieJar;
import top.jfunc.common.http.holderrequest.impl.HolderGetRequest;
import top.jfunc.common.http.smart.*;
import top.jfunc.common.utils.MultiValueMap;

import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author xiongshiyan at 2019/5/20 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@Ignore
public class CookieTest {
    @Rule
    public MockServerRule server = new MockServerRule(this, 50000);

    @Test
    @Ignore
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
        //Map<String, List<String>> stringListMap = manager.get(new URL("").toURI(), null);
        /**其他的操作省略*/
        //从返回的header中获取cookie保存 responseheader就是返回的header
        //manager.put(new URL("").toURI() , null);

        //写入request

        /*cookies.forEach(httpCookie -> {
            String cookie = httpCookie.toString();
            System.out.println("Cookie:" + cookie);
        });
*/
        // 这里我们就获取到了cookie，将其返回。

        //return cookies;
    }


    /**
     * 一个全局的CookieJar
     */
    private CookieJar cookieJar;

    @Before
    public void init(){
        // 创建一个 CookieManager对象
        CookieManager manager = new CookieManager();
        // 接受所有的Cookie
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        // 保存这个定制的CookieManager
        //可调可不调
        // CookieHandler.setDefault(manager);

        this.cookieJar = new JdkCookieJar(manager , true);
    }

    @After
    public void destroy(){
        cookieJar = null;
    }

    @Test
    public void testCookieOkHttp3()throws Exception{
        //只要设置了CookieHandler就支持Cookie
        SmartHttpClient smartHttpClient = new OkHttp3SmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieJar(cookieJar));
        testCookie(smartHttpClient);
    }
    @Test
    public void testCookieApacheHttp()throws Exception{
        //只要设置了CookieHandler就支持Cookie
        SmartHttpClient smartHttpClient = new ApacheSmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieJar(cookieJar));
        testCookie(smartHttpClient);
    }
    @Test
    public void testCookieNativeHttp()throws Exception{
        //只要设置了CookieHandler就支持Cookie
        SmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieJar(cookieJar));
        testCookie(smartHttpClient);
    }
    @Test
    public void testCookieJoddHttp() throws Exception{
        //只要设置了CookieHandler就支持Cookie
        SmartHttpClient smartHttpClient = new JoddSmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieJar(cookieJar));
        testCookie(smartHttpClient);
    }

    private void testCookie(SmartHttpClient smartHttpClient) throws Exception{
        MockServerClient mockClient = new MockServerClient("127.0.0.1", 50000);
        mockClient.when(
                request()
                        .withPath("/hello/getCookie")
                        .withMethod("GET") )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("body1")
                                .withCookie("cookie1" , "cookieValue1")
                                .withCookie("cookie2" , "cookieValue2")
                );

        mockClient.when(
                request()
                        .withPath("/hello/setCookie")
                        .withMethod("GET")
                        .withCookie("cookie1" , "cookieValue1")
                        .withCookie("cookie2" , "cookieValue2")
                        //.withCookie("c","c")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("body2")
                );



        //服务端设置Cookie
        Response response = smartHttpClient.get(HolderGetRequest.of("http://localhost:50000/hello/getCookie"));
        MultiValueMap<String, String> headers = response.getHeaders();
        List<String> cookies = headers.get("set-cookie");
        List<String> expected = new ArrayList<>(2);
        expected.add("cookie1=cookieValue1");
        expected.add("cookie2=cookieValue2");
        Assert.assertEquals(new HashSet<>(cookies) , new HashSet<>(expected));

        //客户端自动带上Cookie
        Response response1 = smartHttpClient.get(HolderGetRequest.of("http://localhost:50000/hello/setCookie"));
        Assert.assertEquals("body2", response1.getBody());
        Assert.assertEquals(200 , response1.getStatusCode());
    }
}
