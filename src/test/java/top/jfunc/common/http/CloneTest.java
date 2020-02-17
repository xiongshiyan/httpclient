package top.jfunc.common.http;

import org.junit.Ignore;
import org.junit.Test;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.holderrequest.impl.HolderUpLoadRequest;
import top.jfunc.common.http.request.RequestCreator;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.basic.UpLoadRequest;

import java.io.File;

/**
 * @author xiongshiyan at 2020/2/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@Ignore
public class CloneTest {
    @Test
    public void testClone() throws Exception{
        /*UpLoadRequest upLoadRequest = UpLoadRequest.of("sss");
        upLoadRequest.addFormParam("k1" , "v1");
        upLoadRequest.setParamCharset("dsd");
        upLoadRequest.addFormFile(new FormFile(new File("C:\\Users\\xiongshiyan\\Desktop\\加班.txt") , "dsad" ,"sda"));
        upLoadRequest.followRedirects(true);


        UpLoadRequest clone = upLoadRequest.clone();

        clone.followRedirects(false);
        clone.setIncludeHeaders(true);
        clone.setIgnoreResponseBody(true);
        clone.addFormParam("k2" , "v2");
        clone.addQueryParam("dd" , "sdsad");
        System.out.println(clone);*/
    }
    @Test
    public void testClone2() throws Exception{
        /*HolderUpLoadRequest upLoadRequest = HolderUpLoadRequest.of("sss");
        upLoadRequest.addFormParam("k1" , "v1");
        upLoadRequest.setParamCharset("dsd");
        upLoadRequest.addFormFile(new FormFile(new File("C:\\Users\\xiongshiyan\\Desktop\\加班.txt") , "dsad" ,"sda"));
        upLoadRequest.followRedirects(true);


        HolderUpLoadRequest clone = upLoadRequest.clone();

        clone.followRedirects(false);
        clone.setIncludeHeaders(true);
        clone.setIgnoreResponseBody(true);
        clone.addFormParam("k2" , "v2");
        clone.addQueryParam("dd" , "sdsad");
        System.out.println(clone);*/
    }

    @Test
    public void testClone3() throws Exception{
        UploadRequest upLoadRequest = UpLoadRequest.of("sss");
        upLoadRequest.addFormParam("k1" , "v1");
        upLoadRequest.setParamCharset("dsd");
        upLoadRequest.addFormFile(new FormFile(new File("C:\\Users\\xiongshiyan\\Desktop\\加班.txt") , "dsad" ,"sda"));
        upLoadRequest.followRedirects(true);


        UploadRequest clone = RequestCreator.clone(upLoadRequest);

        upLoadRequest.setResultCharset("sss");
        clone.addAttribute("a" , "a");

        System.out.println(clone);
    }
}
