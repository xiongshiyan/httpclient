package top.jfunc.common.http;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.basic.*;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import org.junit.Ignore;
import org.junit.Test;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static top.jfunc.common.http.HttpConstants.JSON_WITH_DEFAULT_CHARSET;

/**
 * @author 熊诗言 2017/11/24
 * @see HttpClient
 */
@Ignore
public class HttpBasicTest {
    @Test
    public void testGetOkHttp3(){
        HttpClient http = new OkHttp3Client();
        testGet(http);
    }
    @Test
    public void testGetApacheHttp(){
        HttpClient http = new ApacheHttpClient();
        testGet(http);
    }
    @Test
    public void testGetNativeHttp(){
        HttpClient http = new NativeHttpClient();
        testGet(http);
    }
    @Test
    public void testGetJoddHttp(){
        HttpClient http = new JoddHttpClient();
        testGet(http);
    }
    private void testGet(HttpClient http){
        String url = "http://localhost:8080/http-server-test/get/query";
//        String url = "https://www.hao123.com/";
        String s = null;
        try {
            Map<String , String> headers = new HashMap<>(1);
            headers.put("saleType" , "2");
            s = http.get(url,null,headers);
            System.out.println(s);

            byte[] bytes = http.getAsBytes(url , ArrayListMultiValueMap.fromMap(headers));
            System.out.println(bytes.length);
            System.out.println(new String(bytes));
            File asFile = http.getAsFile(url, new File("C:\\Users\\xiongshiyan\\Desktop\\xxxx.txt"));
            System.out.println(asFile.getAbsolutePath());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Test
    public void testPostOkHttp3(){
        HttpClient http = new OkHttp3Client();
        testPost(http);
    }
    @Test
    public void testPostApacheHttp(){
        HttpClient http = new ApacheHttpClient();
        testPost(http);
    }
    @Test
    public void testPostNativeHttp(){
        HttpClient http = new NativeHttpClient();
        testPost(http);
    }
    @Test
    public void testPostJoddHttp(){
        HttpClient http = new JoddHttpClient();
        testPost(http);
    }
    private void testPost(HttpClient http){
        Map<String , String> headers = new HashMap<>(2);
        headers.put("empCode" , "ahg0023");
        headers.put("phone" , "15208384257");
        try {
            String s = null;
            String url = "http://localhost:8080/http-server-test/post/body";
            s = http.post(url,"{\"name\":\"熊诗言\"}", JSON_WITH_DEFAULT_CHARSET,headers,15000,15000);
            System.out.println(s);

            Map<String , String> params = new HashMap<>(2);
            params.put("empCode" , "ahg0023");
            params.put("phone" , "15208384257");
            url = "http://localhost:8080/http-server-test/post/form";
            String post = http.post(url, params);
            System.out.println(post);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("超时异常");
        }
    }
    @Test
    public void testUploadOkHttp3(){
        HttpClient http = new OkHttp3Client();
        testUploadImpl(http);
    }
    @Test
    public void testUploadApache(){
        HttpClient http = new ApacheHttpClient();
        testUploadImpl(http);
    }
    @Test
    public void testUploadLocal(){
        HttpClient http = new NativeHttpClient();
        testUploadImpl(http);
    }
    @Test
    public void testUploadJodd(){
        HttpClient http = new JoddHttpClient();
        testUploadImpl(http);
    }
    private void testUploadImpl(HttpClient httpClient){
        String url = "http://localhost:8080/http-server-test/upload/only";
        try {
            FormFile formFile = new FormFile(new File("E:\\BugReport.png") , "file",null);
            MultiValueMap<String , String> headers = new ArrayListMultiValueMap<>(2);
            headers.add("empCode" , "ahg0023");
            headers.add("phone" , "15208384257");
            String s = httpClient.upload(url , headers, formFile);
            System.out.println(s);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("超时异常");
        }
    }





    @Test
    public void testUploadWithParamsOkHttp3(){
        HttpClient http = new OkHttp3Client();
        testUploadImplWithParams(http);
    }
    @Test
    public void testUploadWithParamsApache(){
        HttpClient http = new ApacheHttpClient();
        testUploadImplWithParams(http);
    }
    @Test
    public void testUploadWithParamsLocal(){
        HttpClient http = new NativeHttpClient();
        testUploadImplWithParams(http);
    }
    @Test
    public void testUploadWithParamsJodd(){
        HttpClient http = new JoddHttpClient();
        testUploadImplWithParams(http);
    }

    private void testUploadImplWithParams(HttpClient httpClient){
        String url = "http://localhost:8080/http-server-test/upload/withParam";
        try {
            FormFile formFile = new FormFile(new File("E:\\BugReport.png") , "file",null);
            MultiValueMap<String , String> headers = new ArrayListMultiValueMap<>(2);
            headers.add("empCode" , "ahg0023");
            headers.add("phone" , "15208384257");
            MultiValueMap<String , String> params = new ArrayListMultiValueMap<>(2);
            params.add("k1" , "v1");
            params.add("k2" , "v2");
            String s = httpClient.upload(url , params , headers, formFile);
            System.out.println(s);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("超时异常");
        }
    }
}
