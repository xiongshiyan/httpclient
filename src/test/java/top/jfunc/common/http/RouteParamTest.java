package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.http.smart.Request;
import top.jfunc.common.http.smart.Response;

/**
 * @author xiongshiyan at 2019/5/16 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class RouteParamTest {
    @Test
    public void testRouteParam() throws Exception{
        Response response = HttpUtil.get(Request.of("http://localhost:8080/http-server-test/get/{do}").addRouteParam("do", "query"));
        Assert.assertEquals("success" , response.asString());
    }
}
