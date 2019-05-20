package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.mockserver.model.Parameter;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.impl.FormBodyRequest;
import top.jfunc.common.http.request.impl.GetRequest;
import top.jfunc.common.http.request.impl.PostBodyRequest;
import top.jfunc.common.http.smart.*;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author xiongshiyan at 2019/5/16 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class MultiRequestTest {
    @Rule
    public MockServerRule server = new MockServerRule(this, 50000);

    @Test
    public void testGetApache() throws Exception{
        testGet(new ApacheSmartHttpClient());
    }
    @Test
    public void testGetNative() throws Exception{
        testGet(new NativeSmartHttpClient());
    }
    @Test
    public void testGetJodd() throws Exception{
        testGet(new JoddSmartHttpClient());
    }
    @Test
    public void testGetOkHttp() throws Exception{
        testGet(new OkHttp3SmartHttpClient());
    }
    private void testGet(SmartHttpClient smartHttpClient) throws Exception{
        MockServerClient mockClient = new MockServerClient("127.0.0.1", 50000);
        String expected = "{ message: 'incorrect username and password combination' }";
        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("GET") )
        .respond(
                response()
                        .withStatusCode(200)
                        .withBody(expected)
        );

        HttpRequest request = GetRequest.of("http://localhost:50000/hello/{name}").addRouteParam("name" , "John");
        Response response = smartHttpClient.get(request);
        Assert.assertEquals(expected , response.asString());
    }
    @Test
    public void testGetQueryParamApache() throws Exception{
        testGetQueryParam(new ApacheSmartHttpClient());
    }
    @Test
    public void testGetQueryParamNative() throws Exception{
        testGetQueryParam(new NativeSmartHttpClient());
    }
    @Test
    public void testGetQueryParamJodd() throws Exception{
        testGetQueryParam(new JoddSmartHttpClient());
    }
    @Test
    public void testGetQueryParamOkHttp() throws Exception{
        testGetQueryParam(new OkHttp3SmartHttpClient());
    }
    private void testGetQueryParam(SmartHttpClient smartHttpClient) throws Exception{
        MockServerClient mockClient = new MockServerClient("127.0.0.1", 50000);
        String expected = "{ message: 'incorrect username and password combination' }";
        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("GET")
                        .withQueryStringParameters(Parameter.param("key1" , "value1") ,
                                Parameter.param("key2" , "value2")))
        .respond(
                response()
                        .withStatusCode(200)
                        .withBody(expected)
        );

        HttpRequest request = GetRequest.of("http://localhost:50000/hello/{name}")
                .addRouteParam("name" , "John").addQueryParam("key1" , "value1").addQueryParam("key2" , "value2");
        Response response = smartHttpClient.get(request);
        Assert.assertEquals(expected , response.asString());
    }
    @Test
    public void testPostApache() throws Exception{
        testPost(new ApacheSmartHttpClient());
    }
    @Test
    public void testPostNative() throws Exception{
        testPost(new NativeSmartHttpClient());
    }
    @Test
    public void testPostJodd() throws Exception{
        testPost(new JoddSmartHttpClient());
    }
    @Test
    public void testPostOkHttp() throws Exception{
        testPost(new OkHttp3SmartHttpClient());
    }
    private void testPost(SmartHttpClient smartHttpClient) throws Exception{
        MockServerClient mockClient = new MockServerClient("127.0.0.1", 50000);
        String expected = "{ message: 'incorrect username and password combination' }";
        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("POST").withBody(expected))
        .respond(
                response()
                        .withStatusCode(200)
                        .withBody(expected)
        );

        StringBodyRequest request = PostBodyRequest.of("http://localhost:50000/hello/{name}").addRouteParam("name" , "John").setBody(expected);
        Response response = smartHttpClient.post(request);
        Assert.assertEquals(expected , response.asString());
    }
    @Test
    public void testPostFormApache() throws Exception{
        testPostForm(new ApacheSmartHttpClient());
    }
    @Test
    public void testPostFormNative() throws Exception{
        testPostForm(new NativeSmartHttpClient());
    }
    @Test
    public void testPostFormJodd() throws Exception{
        testPostForm(new JoddSmartHttpClient());
    }
    @Test
    public void testPostFormOkHttp() throws Exception{
        testPostForm(new OkHttp3SmartHttpClient());
    }
    private void testPostForm(SmartHttpClient smartHttpClient) throws Exception{
        MockServerClient mockClient = new MockServerClient("127.0.0.1", 50000);
        String expected = "{ message: 'incorrect username and password combination' }";
        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("POST")
                        .withBody("key1=value1&key2=value2")
                        .withHeader(Header.header("Content-Type" , HttpConstants.FORM_URLENCODED_WITH_DEFAULT_CHARSET)))
        .respond(
                response()
                        .withStatusCode(200)
                        .withBody(expected)
        );

        StringBodyRequest request = FormBodyRequest.of("http://localhost:50000/hello/{name}").addRouteParam("name" , "John")
                .addFormParam("key1" , "value1").addFormParam("key2" , "value2");
        Response response = smartHttpClient.post(request);
        Assert.assertEquals(expected , response.asString());
    }
    @Test
    public void testHeaderApache() throws Exception{
        testHeader(new ApacheSmartHttpClient());
    }
    @Test
    public void testHeaderNative() throws Exception{
        testHeader(new NativeSmartHttpClient());
    }
    @Test
    public void testHeaderJodd() throws Exception{
        testHeader(new JoddSmartHttpClient());
    }
    @Test
    public void testHeaderOkHttp() throws Exception{
        testHeader(new OkHttp3SmartHttpClient());
    }
    private void testHeader(SmartHttpClient smartHttpClient) throws Exception{
        MockServerClient mockClient = new MockServerClient("127.0.0.1", 50000);
        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("GET").withHeader(Header.header("sale" , "2")))
        .respond(
                response()
                        .withStatusCode(200)
                        .withHeader(Header.header("xx" , "xx"))
        );

        HttpRequest request = GetRequest.of("http://localhost:50000/hello/{name}").addRouteParam("name" , "John")
                .addHeader("sale" , "2").addHeader("ca-xx" , "ca-xx").setIncludeHeaders(true);
        Response response = smartHttpClient.get(request);
        Assert.assertEquals("xx" , response.getOneHeader("xx"));
    }

    @Test
    public void testAllApache() throws Exception{
        testAll(new ApacheSmartHttpClient());
    }
    @Test
    public void testAllNative() throws Exception{
        testAll(new NativeSmartHttpClient());
    }
    @Test
    public void testAllJodd() throws Exception{
        testAll(new JoddSmartHttpClient());
    }
    @Test
    public void testAllOkHttp() throws Exception{
        testAll(new OkHttp3SmartHttpClient());
    }
    private void testAll(SmartHttpClient smartHttpClient) throws Exception{

        StringBodyRequest request = PostBodyRequest.of("http://localhost:8080/http-server-test/post/all")
                .addHeader("sale" , "2").addHeader("ca-xx" , "ca-xx").setIncludeHeaders(true)
                .setBody("xxxxx" , HttpConstants.JSON_WITH_DEFAULT_CHARSET)
                .addQueryParam("sa" , "sa").addQueryParam("ds" , "ds");
        Response response = smartHttpClient.post(request);
        Assert.assertEquals("success" , response.asString());
        System.out.println(response.getHeaders());
    }
}
