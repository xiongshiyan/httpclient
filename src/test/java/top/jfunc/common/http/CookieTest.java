package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.cookie.CookieStore;
import top.jfunc.common.http.cookie.InMemoryCookieStore;
import top.jfunc.common.http.holderrequest.impl.HolderGetRequest;
import top.jfunc.common.http.smart.*;
import top.jfunc.common.utils.MultiValueMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author xiongshiyan at 2019/5/20 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@Ignore
public class CookieTest {
    @Rule
    public MockServerRule server = new MockServerRule(this, 50000);

    private CookieStore cookieStore = new InMemoryCookieStore();

    @Test
    public void testCookieOkHttp3()throws Exception{
        //只要设置了CookieStore就支持Cookie
        SmartHttpClient smartHttpClient = new OkHttp3SmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieStore(cookieStore));
        testCookie(smartHttpClient);
    }
    @Test
    public void testCookieApacheHttp()throws Exception{
        //只要设置了CookieStore就支持Cookie
        SmartHttpClient smartHttpClient = new ApacheSmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieStore(cookieStore));
        testCookie(smartHttpClient);
    }
    @Test
    public void testCookieNativeHttp()throws Exception{
        //只要设置了CookieStore就支持Cookie
        SmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieStore(cookieStore));
        testCookie(smartHttpClient);
    }
    @Test
    public void testCookieJoddHttp() throws Exception{
        //只要设置了CookieStore就支持Cookie
        SmartHttpClient smartHttpClient = new JoddSmartHttpClient();
        smartHttpClient.setConfig(Config.defaultConfig().setCookieStore(cookieStore));
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
        Assert.assertEquals("body2", response1.getBodyAsString());
        Assert.assertEquals(200 , response1.getStatusCode());
    }
}
