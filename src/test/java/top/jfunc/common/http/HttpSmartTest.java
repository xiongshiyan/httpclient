package top.jfunc.common.http;

import org.junit.Assert;
import top.jfunc.common.http.holderrequest.impl.HolderCommonBodyRequest;
import top.jfunc.common.string.FromString;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.holderrequest.impl.HolderPostBodyRequest;
import top.jfunc.common.http.smart.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static top.jfunc.common.http.HttpConstants.FORM_URLENCODED_WITH_DEFAULT_CHARSET;
import static top.jfunc.common.http.HttpConstants.JSON_WITH_DEFAULT_CHARSET;

/**
 * @author 熊诗言 2017/11/24
 * @see SmartHttpClient
 */
@Ignore
public class HttpSmartTest {
    @Test
    public void testCalculateBodyCharset(){
        /*NativeSmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        //bodyCharset指定了就是他
        Assert.assertThat(smartHttpClient.calculateBodyCharset("GB2312" , null) , is("GB2312"));
        //contentType指定了就是他
        Assert.assertThat(smartHttpClient.calculateBodyCharset(null , "application/json;charset=GBK") , is("GBK"));
        //都未指定就是全局默认
        Assert.assertThat(smartHttpClient.calculateBodyCharset(null , "application/json") , is(HttpConstants.DEFAULT_CHARSET));
        Assert.assertThat(smartHttpClient.calculateBodyCharset(null , null) , is(HttpConstants.DEFAULT_CHARSET));*/
    }

    @Test
    public void testGetOkHttp3(){
        OkHttp3SmartHttpClient http = new OkHttp3SmartHttpClient();
        http.getConfig().setBaseUrl("http://localhost:8080/http-server-test/");
        testGet(http);
    }
    @Test
    public void testGetApacheHttp(){
        ApacheSmartHttpClient http = new ApacheSmartHttpClient();
        http.getConfig().setBaseUrl("http://localhost:8080/http-server-test/");
        testGet(http);
    }
    @Test
    public void testGetNativeHttp(){
        NativeSmartHttpClient http = new NativeSmartHttpClient();
        http.getConfig().setBaseUrl("http://localhost:8080/http-server-test/");
        testGet(http);
    }
    @Test
    public void testGetJoddHttp(){
        JoddSmartHttpClient http = new JoddSmartHttpClient();
        http.getConfig().setBaseUrl("http://localhost:8080/http-server-test/");
        testGet(http);
    }
    private void testGet(SmartHttpClient http){
        String url = "get/query";
        try {
            Request request1 = Request.of(url).setIgnoreResponseBody(false).setIncludeHeaders(true).setResultCharset("UTF-8");
            request1.headerHolder().addHeader("saleType" , "2");
            Response response = http.get(request1);
            System.out.println(response);
            System.out.println("headerHolder:" + response.getHeaders());

            String s = http.get(url);
            System.out.println(s);

            Request request = Request.of(url).setResultCharset("UTF-8");
            request.headerHolder().addHeader("saleType" , "2");
            byte[] bytes = http.getAsBytes(request);
            System.out.println(bytes.length);
            System.out.println(new String(bytes));

            request = Request.of(url);
            request.fileHolder().setFile(new File("C:\\Users\\xiongshiyan\\Desktop\\yyyy.txt"));
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
    @Test
    public void testPostJoddHttp(){
        SmartHttpClient http = new JoddSmartHttpClient();
        testPost(http);
    }
    @Test
    public void testPostGBKOkHttp3(){
        SmartHttpClient http = new OkHttp3SmartHttpClient();
        testPostGBK(http);
    }
    @Test
    public void testPostGBKApacheHttp(){
        SmartHttpClient http = new ApacheSmartHttpClient();
        testPostGBK(http);
    }
    @Test
    public void testPostGBKNativeHttp(){
        SmartHttpClient http = new NativeSmartHttpClient();
        testPostGBK(http);
    }
    @Test
    public void testPostGBKJoddHttp(){
        SmartHttpClient http = new JoddSmartHttpClient();
        testPostGBK(http);
    }
    public void testPostGBK(SmartHttpClient http){
        try {
            String url = "http://localhost:8080/http-server-test/post/bodyGBK";
            String charset = "GBK";
            Request request = Request.of(url).setResultCharset("UTF-8").setBodyCharset(charset)
                    //设置ContentType非常重要，尤其是对于HTTPURLConnection来说，他写入的时候根据这个编码来写的
                    .setContentType(MediaType.APPLICATIPON_JSON.withCharset(charset));
            request.bodyHolder().setBody("熊诗言");
            Response post = http.post(request);
            System.out.println(post.getBody());
        }catch (IOException e){
            System.out.println("超时异常");
        }
    }

    public void testPost(SmartHttpClient http){
        try {
            String url = "http://localhost:8080/http-server-test/post/body";
            Request request = Request.of(url).setIncludeHeaders(true).setContentType(JSON_WITH_DEFAULT_CHARSET).setConnectionTimeout(10000).setReadTimeout(10000).setResultCharset("UTF-8");
            request.bodyHolder().setBody("{\"name\":\"熊诗言\"}");
            request.headerHolder().addHeader("ss" , "ss").addHeader("ss" , "dd");
            Response post = http.post(request);
            System.out.println(post.getBody());
            System.out.println(post.getHeaders());

            String s = http.postJson(url, "{\"name\":\"熊诗言\"}");
            System.out.println(s);

            url = "http://localhost:8080/http-server-test/post/form";
            request = Request.of(url).setContentType(FORM_URLENCODED_WITH_DEFAULT_CHARSET);
            request.formParamHolder().addParam("xx" , "xx").addParam("yy" , "yy");
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
    @Test
    public void testUploadJodd(){
        SmartHttpClient http = new JoddSmartHttpClient();
        testUploadImpl(http);
    }
    private void testUploadImpl(SmartHttpClient httpClient){
        String url = "http://localhost:8080/http-server-test/upload/only";
        try {
            FormFile formFile = new FormFile(new File("E:\\BugReport.png") , "file",null);
            Request request = Request.of(url).setIncludeHeaders(true);
            request.formFileHolder().addFormFile(formFile);
            request.headerHolder().addHeader("empCode" , "ahg0023")
                    .addHeader("phone" , "15208384257");
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
    @Test
    public void testUploadWithParamsJodd(){
        SmartHttpClient http = new JoddSmartHttpClient();
        testUploadImplWithParams(http);
    }

    private void testUploadImplWithParams(SmartHttpClient httpClient){
        String url = "http://localhost:8080/http-server-test/upload/withParam";
        try {
            FormFile formFile = new FormFile(new File("E:\\BugReport.3png") , "file",null);
            FormFile formFile2 = new FormFile(new File("E:\\BugReport.png") , "file2",null);
            Request request = Request.of(url).setIncludeHeaders(true);
            request.formFileHolder().addFormFile(formFile2).addFormFile(formFile);
            request.headerHolder().addHeader("empCode" , "ahg0023")
                    .addHeader("phone" , "15208384257");
            request.formParamHolder().addParam("k1", "v1").addParam("k2" , "v2");
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
    @Test
    public void testHttpMethodJoddHttp(){
        SmartHttpClient http = new JoddSmartHttpClient();
        testHttpMethod(http);
    }
    public void testHttpMethod(SmartHttpClient http){
//        String url = "https://dzg.palmte.cn/dzdsds";
        String url = "http://localhost:8080/http-server-test/put/body";
        try {
            Request request = Request.of(url).setIncludeHeaders(true).setContentType(JSON_WITH_DEFAULT_CHARSET).setConnectionTimeout(10000).setReadTimeout(10000).setResultCharset("UTF-8");
            request.bodyHolder().setBody("{\"name\":\"熊诗言\"}");
            request.headerHolder().addHeader("ss" , "ss").addHeader("ss" , "dd");
            Response response = http.http(request , Method.PUT);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        }catch (IOException e){
            System.out.println("超时异常");
        }
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

        HolderCommonBodyRequest request = HolderPostBodyRequest.of("http://localhost:8080/http-server-test/post/all")
                .setIncludeHeaders(true).setBody("xxxxx", HttpConstants.JSON_WITH_DEFAULT_CHARSET);
        request.headerHolder().addHeader("sale" , "2").addHeader("ca-xx" , "ca-xx");
        request.queryParamHolder().addParam("sa" , "sa").addParam("ds" , "ds");
        Response response = smartHttpClient.post(request);
        Assert.assertEquals("success" , response.asString());
        System.out.println(response.getHeaders());
    }


    @Test
    public void testRequest(){
        Request request = Request.of("https://wwww.baidu.com");
        request.bodyHolder().setBodyT(new SomeBean("ss"), SomeBean::getSs);
        Assert.assertEquals("ss" , request.getBody());
    }
    @Test
    public void testRequest2(){
        Request request = Request.of("https://wwww.baidu.com");
        request.bodyHolder().setBody(new SomeBean("ss"), (o)->"sssss");
        Assert.assertEquals("sssss" , request.getBody());
    }

    @Test
    public void testResponse(){
        Response response = Response.with(200 , new ByteArrayInputStream("ss".getBytes()) , "UTF-8" , null);
        SomeBean someBean = response.as(SomeBean.class, (s , c)->{
                    Assert.assertEquals("ss", s);
                    Assert.assertEquals(c , SomeBean.class);
                    return new SomeBean(s);
                }
        );
        Assert.assertEquals("ss" , someBean.getSs());
    }
    @Test
    public void testResponse2(){
        Response response = Response.with(200 , new ByteArrayInputStream("ss".getBytes()) , "UTF-8" , null);
        SomeBean someBean = response.asT(SomeBean.class, new FromString() {
                    @Override
                    public <T> T as(String src, Class<T> toClass) {
                        T t = null;
                        try {
                            t = toClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return t;
                    }
                }
        );
        Assert.assertEquals(SomeBean.class , someBean.getClass());
    }
    private static class SomeBean{
        private String ss;

        public SomeBean() {
        }

        public SomeBean(String ss) {
            this.ss = ss;
        }

        public String getSs() {
            return ss;
        }

        public void setSs(String ss) {
            this.ss = ss;
        }
    }
}
