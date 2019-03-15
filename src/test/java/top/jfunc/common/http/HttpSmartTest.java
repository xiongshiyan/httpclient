package top.jfunc.common.http;

import top.jfunc.common.http.basic.FormFile;
import top.jfunc.common.http.smart.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static top.jfunc.common.http.HttpConstants.FORM_URLENCODED;
import static top.jfunc.common.http.HttpConstants.JSON_WITH_DEFAULT_CHARSET;

/**
 * @author 熊诗言 2017/11/24
 * @see SmartHttpClient
 */
@Ignore
public class HttpSmartTest {
    @Test
    public void testGetOkHttp3(){
        SmartHttpClient http = new OkHttp3SmartHttpClient();
        testGet(http);
    }
    @Test
    public void testGetApacheHttp(){
        SmartHttpClient http = new ApacheSmartHttpClient();
        testGet(http);
    }
    @Test
    public void testGetNativeHttp(){
        SmartHttpClient http = new NativeSmartHttpClient();
        testGet(http);
    }
    private void testGet(SmartHttpClient http){
        String url = "http://localhost:8183/dzg/api/v2/h5/common/getIp";
        try {
            Response response = http.get(Request.of(url).addHeader("saleType" , "2").setResultCharset("UTF-8"));
            System.out.println(response);
            System.out.println(response.getHeaders());

            String s = http.get(url);
            System.out.println(s);

            Request request = Request.of(url).addHeader("saleType" , "2").setResultCharset("UTF-8");
            byte[] bytes = http.getAsBytes(request);
            System.out.println(bytes.length);
            System.out.println(new String(bytes));

            request = Request.of(url).setFile(new File("C:\\Users\\xiongshiyan\\Desktop\\yyyy.txt"));
            File asFile = http.getAsFile(request);
            System.out.println(asFile.getAbsolutePath());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Test
    public void testPostOkHttp3(){
        SmartHttpClient http = new OkHttp3SmartHttpClient();
        testPost(http);
    }
    @Test
    public void testPostApacheHttp(){
        SmartHttpClient http = new ApacheSmartHttpClient();
        testPost(http);
    }
    @Test
    public void testPostNativeHttp(){
        SmartHttpClient http = new NativeSmartHttpClient();
        testPost(http);
    }
    public void testPost(SmartHttpClient http){
        String url = "http://localhost:8183/dzg/api/v2/test/boss";
        try {
            Request request = Request.of(url).setIncludeHeaders(true).addHeader("ss" , "ss").addHeader("ss" , "dd").setBody("{\"name\":\"熊诗言\"}").setContentType(JSON_WITH_DEFAULT_CHARSET).setConnectionTimeout(10000).setReadTimeout(10000).setResultCharset("UTF-8");
            Response post = http.post(request);
            System.out.println(post.getBody());
            System.out.println(post.getHeaders());

            String s = http.postJson(url, "{\"name\":\"熊诗言\"}");
            System.out.println(s);

            request = Request.of(url).addParam("xx" , "xx").addParam("yy" , "yy").setContentType(FORM_URLENCODED);
            Response response = http.post(request);
            System.out.println(response.getBody());
        }catch (IOException e){
            System.out.println("超时异常");
        }
    }

    @Test
    public void testUploadOkHttp3(){
        SmartHttpClient http = new OkHttp3SmartHttpClient();
        testUploadImpl(http);
    }
    @Test
    public void testUploadApache(){
        SmartHttpClient http = new ApacheSmartHttpClient();
        testUploadImpl(http);
    }
    @Test
    public void testUploadLocal(){
        SmartHttpClient http = new NativeSmartHttpClient();
        testUploadImpl(http);
    }
    private void testUploadImpl(SmartHttpClient httpClient){
        String url = "http://localhost:8183/dzg/api/v2/common/VPFileUpload";
        try {
            FormFile formFile = new FormFile(new File("E:\\838586397836550106.jpg") , "filedata",null);
            Request request = Request.of(url).addHeader("empCode" , "ahg0023")
                    .addHeader("phone" , "15208384257").addFormFile(formFile).setIncludeHeaders(true);
            Response response = httpClient.upload(request);
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("超时异常");
        }
    }


    @Test
    public void testUploadWithParamsOkHttp3(){
        SmartHttpClient http = new OkHttp3SmartHttpClient();
        testUploadImplWithParams(http);
    }
    @Test
    public void testUploadWithParamsApache(){
        SmartHttpClient http = new ApacheSmartHttpClient();
        testUploadImplWithParams(http);
    }
    @Test
    public void testUploadWithParamsLocal(){
        SmartHttpClient http = new NativeSmartHttpClient();
        testUploadImplWithParams(http);
    }

    private void testUploadImplWithParams(SmartHttpClient httpClient){
        String url = "http://localhost:8183/dzg/api/v2/common/VPFileUpload";
        try {
            FormFile formFile = new FormFile(new File("E:\\838586397836550106.jpg") , "filedata",null);
            FormFile formFile2 = new FormFile(new File("E:\\BugReport.png") , "filedata2",null);
            Request request = Request.of(url).addHeader("empCode" , "ahg0023")
                    .addHeader("phone" , "15208384257").addFormFile(formFile2).addFormFile(formFile).setIncludeHeaders(true);

            request.addParam("k1", "v1").addParam("k2" , "v2");
            Response response = httpClient.upload(request);
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("超时异常");
        }
    }

    @Test
    public void testHttpMethodOkHttp3(){
        SmartHttpClient http = new OkHttp3SmartHttpClient();
        testHttpMethod(http);
    }
    @Test
    public void testHttpMethodApacheHttp(){
        SmartHttpClient http = new ApacheSmartHttpClient();
        testHttpMethod(http);
    }
    @Test
    public void testHttpMethodNativeHttp(){
        SmartHttpClient http = new NativeSmartHttpClient();
        testHttpMethod(http);
    }
    public void testHttpMethod(SmartHttpClient http){
//        String url = "https://dzg.palmte.cn/dzdsds";
        String url = "http://localhost:8183/dzg/api/v2/test/boss";
        try {
            Request request = Request.of(url).setIncludeHeaders(true).addHeader("ss" , "ss").addHeader("ss" , "dd").setBody("{\"name\":\"熊诗言\"}").setContentType(JSON_WITH_DEFAULT_CHARSET).setConnectionTimeout(10000).setReadTimeout(10000).setResultCharset("UTF-8");
            Response response = http.httpMethod(request , Method.PUT);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        }catch (IOException e){
            System.out.println("超时异常");
        }
    }
}
