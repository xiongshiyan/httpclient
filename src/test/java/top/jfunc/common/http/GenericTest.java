package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.MediaType;
import top.jfunc.common.http.smart.Request;

/**
 * @author xiongshiyan at 2019/6/21 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class GenericTest {
    /**
     * 测试方法连缀
     */
    @Test
    public void testThis(){
        Request request = Request.of("https://somedomain:port/get/{id}").addHeader("","")
                .addFormParam("k1","v1").addRouteParam("id","3");
    }
    @Test
    public void testMediaType(){
        Assert.assertEquals("application/json" , MediaType.APPLICATIPON_JSON.toString());
        Assert.assertEquals("application/json;charset=utf-8" , MediaType.APPLICATIPON_JSON.withCharset(Config.DEFAULT_CHARSET).toString());
        Assert.assertEquals("application/x-www-form-urlencoded" , MediaType.APPLICATIPON_FORM_DATA.toString());
        Assert.assertEquals("application/x-www-form-urlencoded;charset=utf-8" , MediaType.APPLICATIPON_FORM_DATA.withCharset(Config.DEFAULT_CHARSET).toString());
    }
}
