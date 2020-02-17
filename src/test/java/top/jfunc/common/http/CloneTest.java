package top.jfunc.common.http;

import org.junit.Test;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.request.basic.UpLoadRequest;

import java.io.File;

/**
 * @author xiongshiyan at 2020/2/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CloneTest {
    @Test
    public void testClone() throws Exception{
        UpLoadRequest upLoadRequest = UpLoadRequest.of("sss");
        upLoadRequest.addFormParam("k1" , "v1");
        upLoadRequest.setParamCharset("dsd");
        upLoadRequest.addFormFile(new FormFile(new File("C:\\Users\\xiongshiyan\\Desktop\\加班.txt") , "dsad" ,"sda"));
        UpLoadRequest clone = upLoadRequest.clone();

        clone.followRedirects(true);
        clone.setIncludeHeaders(true);
        clone.setIgnoreResponseBody(true);
        clone.addFormParam("k2" , "v2");
        clone.addQueryParam("dd" , "sdsad");
        System.out.println(clone);
    }
}
