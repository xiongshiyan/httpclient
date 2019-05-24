package top.jfunc.common.http;

import org.junit.Ignore;
import org.junit.Test;
import top.jfunc.common.http.interfacing.JfuncHttp;
import top.jfunc.common.http.smart.ApacheSmartHttpClient;
import top.jfunc.common.http.smart.Response;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@Ignore
public class JfuncHttpTest {
    @Test
    public void testGetInterface(){
        JfuncHttp jfuncHttp = new JfuncHttp().setSmartHttpClient(new ApacheSmartHttpClient());
        Get_Interface getInterface = jfuncHttp.create(Get_Interface.class);
        //Response zzzzzz = getInterface.request(GetRequest.of("https://www.baidu.com"));
        //System.out.println(zzzzzz);

        Response zzzzz = getInterface.list(1, "zzzzz");
    }
}
