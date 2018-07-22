package cn.zytx.common.http;

import cn.zytx.common.http.basic.*;
import cn.zytx.common.utils.ArrayListMultimap;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static cn.zytx.common.http.HttpConstants.JSON_WITH_DEFAULT_CHARSET;

/**
 * @author 熊诗言 2017/11/24
 * @see HttpClient
 */
public class HttpBasicTest {
    @Test@Ignore
    public void testGetOkHttp3(){
        HttpClient http = new OkHttp3Client();
        testGet(http);
    }
    @Test@Ignore
    public void testGetApacheHttp(){
        HttpClient http = new ApacheHttpClient();
        testGet(http);
    }
    @Test@Ignore
    public void testGetNativeHttp(){
        HttpClient http = new NativeHttpClient();
        testGet(http);
    }
    private void testGet(HttpClient http){
        String url = "https://dzgtest.palmte.cn/dzg/api/v2/h5/common/getIp";
//        String url = "https://www.hao123.com/";
        String s = null;
        try {
            Map<String , String> headers = new HashMap<>(1);
            headers.put("saleType" , "2");
            s = http.get(url,null,headers);
            System.out.println(s);

            byte[] bytes = http.getAsBytes(url , ArrayListMultimap.fromMap(headers));
            System.out.println(bytes.length);
            System.out.println(new String(bytes));
            File asFile = http.getAsFile(url, new File("C:\\Users\\xiongshiyan\\Desktop\\xxxx.txt"));
            System.out.println(asFile.getAbsolutePath());
        }catch (IOException e){
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
            System.out.println(e.getResponseCode());
            System.out.println(e.getErrorMessage());
        }
    }
    @Test@Ignore
    public void testPostOkHttp3(){
        HttpClient http = new OkHttp3Client();
        testPost(http);
    }
    @Test@Ignore
    public void testPostApacheHttp(){
        HttpClient http = new ApacheHttpClient();
        testPost(http);
    }
    @Test@Ignore
    public void testPostNativeHttp(){
        HttpClient http = new NativeHttpClient();
        testPost(http);
    }
    private void testPost(HttpClient http){
        String url = "https://dzgtest.palmte.cn/dzg/api/v2/test/boss";
        Map<String , String> headers = new HashMap<>(2);
        headers.put("empCode" , "ahg0023");
        headers.put("phone" , "15208384257");
        try {
            String s = null;
            s = http.post(url,"{\"name\":\"熊诗言\"}", JSON_WITH_DEFAULT_CHARSET,headers,15000,15000);
            System.out.println(s);

            Map<String , String> params = new HashMap<>(2);
            params.put("empCode" , "ahg0023");
            params.put("phone" , "15208384257");
            String post = http.post(url, params);
            System.out.println(post);
        } catch (HttpException e) {
            e.printStackTrace();
            System.out.println(e.getResponseCode());
            System.out.println(e.getErrorMessage());
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("超时异常");
        }
    }
    @Test@Ignore
    public void testUploadOkHttp3(){
        HttpClient http = new OkHttp3Client();
        testUploadImpl(http);
    }
    @Test@Ignore
    public void testUploadApache(){
        HttpClient http = new ApacheHttpClient();
        testUploadImpl(http);
    }
    @Test@Ignore
    public void testUploadLocal(){
        HttpClient http = new NativeHttpClient();
        testUploadImpl(http);
    }
    private void testUploadImpl(HttpClient httpClient){
        String url = "http://localhost:8183/dzg/api/v2/common/VPFileUpload";
        try {
            FormFile formFile = new FormFile(new File("E:\\838586397836550106.jpg") , "filedata",null);
            ArrayListMultimap<String , String> headers = new ArrayListMultimap<>(2);
            headers.put("empCode" , "ahg0023");
            headers.put("phone" , "15208384257");
            String s = httpClient.upload(url , headers, formFile);
            System.out.println(s);
        } catch (HttpException e) {
            e.printStackTrace();
            System.out.println(e.getResponseCode());
            System.out.println(e.getErrorMessage());
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("超时异常");
        }
    }





    @Test@Ignore
    public void testUploadWithParamsOkHttp3(){
        HttpClient http = new OkHttp3Client();
        testUploadImplWithParams(http);
    }
    @Test@Ignore
    public void testUploadWithParamsApache(){
        HttpClient http = new ApacheHttpClient();
        testUploadImplWithParams(http);
    }
    @Test@Ignore
    public void testUploadWithParamsLocal(){
        HttpClient http = new NativeHttpClient();
        testUploadImplWithParams(http);
    }

    private void testUploadImplWithParams(HttpClient httpClient){
        String url = "http://localhost:8183/dzg/api/v2/common/VPFileUpload";
        try {
            FormFile formFile = new FormFile(new File("E:\\BugReport.png") , "filedata",null);
            ArrayListMultimap<String , String> headers = new ArrayListMultimap<>(2);
            headers.put("empCode" , "ahg0023");
            headers.put("phone" , "15208384257");
            ArrayListMultimap<String , String> params = new ArrayListMultimap<>(2);
            params.put("k1" , "v1");
            params.put("k2" , "v2");
            String s = httpClient.upload(url , params , headers, formFile);
            System.out.println(s);
        } catch (HttpException e) {
            e.printStackTrace();
            System.out.println(e.getResponseCode());
            System.out.println(e.getErrorMessage());
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("超时异常");
        }
    }
}
