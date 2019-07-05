package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.mockserver.model.Parameter;
import top.jfunc.common.http.req.HttpRequest;
import top.jfunc.common.http.req.StringBodyRequest;
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

    private static final String BODY = "{ message: 'incorrect username and password combination' }";

    @Test
    public void testJdk() throws Exception{
        NativeSmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        testAll(smartHttpClient);
    }
    @Test
    public void testApache() throws Exception{
        ApacheSmartHttpClient smartHttpClient = new ApacheSmartHttpClient();
        testAll(smartHttpClient);
    }
    @Test
    public void testOkhttp3() throws Exception{
        OkHttp3SmartHttpClient smartHttpClient = new OkHttp3SmartHttpClient();
        testAll(smartHttpClient);
    }
    @Test
    public void testJodd() throws Exception{
        JoddSmartHttpClient smartHttpClient = new JoddSmartHttpClient();
        testAll(smartHttpClient);
    }

    private void testAll(SmartHttpClient smartHttpClient) throws Exception {
        testGet(smartHttpClient);
        testGetQueryParam(smartHttpClient);
        testPost(smartHttpClient);
        testPostForm(smartHttpClient);
        testHeader(smartHttpClient);
    }
    private void testGet(SmartHttpClient smartHttpClient) throws Exception{
        MockServerClient mockClient = new MockServerClient("127.0.0.1", 50000);
        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("GET") )
        .respond(
                response()
                        .withStatusCode(200)
                        .withBody(BODY)
        );

        HttpRequest request = GetRequest.of("http://localhost:50000/hello/{name}");
        request.addRouteParam("name" , "John");
        Response response = smartHttpClient.get(request);
        Assert.assertEquals(BODY , response.asString());
    }
    private void testGetQueryParam(SmartHttpClient smartHttpClient) throws Exception{
        MockServerClient mockClient = new MockServerClient("127.0.0.1", 50000);
        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("GET")
                        .withQueryStringParameters(Parameter.param("key1" , "value1") ,
                                Parameter.param("key2" , "value2")))
        .respond(
                response()
                        .withStatusCode(200)
                        .withBody(BODY)
        );

        HttpRequest request = GetRequest.of("http://localhost:50000/hello/{name}");
        request.addRouteParam("name" , "John");
        request.addQueryParam("key1" , "value1").addQueryParam("key2" , "value2");
        Response response = smartHttpClient.get(request);
        Assert.assertEquals(BODY , response.asString());
    }
    private void testPost(SmartHttpClient smartHttpClient) throws Exception{
        MockServerClient mockClient = new MockServerClient("127.0.0.1", 50000);
        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("POST").withBody(BODY))
        .respond(
                response()
                        .withStatusCode(200)
                        .withBody(BODY)
        );

        StringBodyRequest request = PostBodyRequest.of("http://localhost:50000/hello/{name}").setBody(BODY);
        request.addRouteParam("name" , "John");
        Response response = smartHttpClient.post(request);
        Assert.assertEquals(BODY , response.asString());
    }
    private void testPostForm(SmartHttpClient smartHttpClient) throws Exception{
        MockServerClient mockClient = new MockServerClient("127.0.0.1", 50000);
        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("POST")
                        .withBody("key1=value1&key2=value2")
                        .withHeader(Header.header("Content-Type" , HttpConstants.FORM_URLENCODED_WITH_DEFAULT_CHARSET)))
        .respond(
                response()
                        .withStatusCode(200)
                        .withBody(BODY)
        );

        FormBodyRequest request = FormBodyRequest.of("http://localhost:50000/hello/{name}");
        request.routeParamHolder().addRouteParam("name" , "John");
        request.formParamHolder().addParam("key1" , "value1").addParam("key2" , "value2");
        Response response = smartHttpClient.post(request);
        Assert.assertEquals(BODY , response.asString());
    }
    private void testHeader(SmartHttpClient smartHttpClient) throws Exception{
        MockServerClient mockClient = new MockServerClient("127.0.0.1", 50000);
        mockClient.reset();
        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("GET").withHeader(Header.header("sale" , "2")))
        .respond(
                response()
                        .withStatusCode(200)
                        .withHeader(Header.header("xx" , "xx"))
        );

        HttpRequest request = GetRequest.of("http://localhost:50000/hello/{name}").setIncludeHeaders(true);
        request.addRouteParam("name" , "John");
        request.addHeader("sale" , "2").addHeader("ca-xx" , "ca-xx");
        Response response = smartHttpClient.get(request);
        Assert.assertEquals("xx" , response.getOneHeader("xx"));
    }
}
