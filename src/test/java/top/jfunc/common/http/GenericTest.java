package top.jfunc.common.http;

import org.junit.Test;
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
}
