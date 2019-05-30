package top.jfunc.common.http;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.interfacing.HttpServiceCreator;
import top.jfunc.common.http.request.impl.GetRequest;
import top.jfunc.common.http.smart.ApacheSmartHttpClient;
import top.jfunc.common.http.smart.Response;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@Ignore
public class HttpServiceCreatorTest {
    Map<String , String> map = new HashMap<>();
    {
        map.put("xx" , "xxx");
        map.put("yy" , "yy");
    }
    InterfaceForTestJfunc jfunc;
    @Before
    public void init(){
        Config config = Config.defaultConfig().setBaseUrl("http://localhost:8080/http-server-test/");
        HttpServiceCreator httpServiceCreator = new HttpServiceCreator().setSmartHttpClient(new ApacheSmartHttpClient().setConfig(config));
        jfunc = httpServiceCreator.create(InterfaceForTestJfunc.class);
    }

    @Test
    public void testRequest(){
        Response zzzzzz = jfunc.request(GetRequest.of("https://www.baidu.com"));
        System.out.println(zzzzzz);
    }
    @Test
    public void testPath(){
        Response zzzzz = jfunc.list("query", 1);
        System.out.println(zzzzz);
    }
    @Test
    public void testQuery(){
        Response query = jfunc.url("get/query");
        System.out.println(query);
    }
    @Test
    public void testQueryMap(){
        Response response = jfunc.queryMap(map);
        System.out.println(response);
    }
    @Test
    public void testHeader(){
        Response header = jfunc.header("xx");
        System.out.println(header);
    }
    @Test
    public void testHeaders(){
        Response headers = jfunc.headers("nakedddd");
        System.out.println(headers);
    }
    @Test
    public void testHeaderMap(){
        Response headerMap = jfunc.headerMap(map);
        System.out.println(headerMap);
    }
    @Test
    public void testDownLoad(){
        Response download = jfunc.download();
        System.out.println(download.asFile(new File("xx.txt")));
    }
    @Test
    public void testPostBody(){
        Response post = jfunc.post("body", "嘻嘻嘻嘻嘻嘻嘻");
        System.out.println(post);
    }
    @Test
    public void testForm(){
        Response xx = jfunc.form("xx", 15);
        System.out.println(xx);
    }
    @Test
    public void testFormMap(){
        Response formMap = jfunc.formMap(map);
        System.out.println(formMap);
    }
    @Test
    public void testUpload(){
        FormFile formFile = new FormFile(new File("F:\\xiongshiyan\\material\\构建工具\\Grunt\\code\\自动化构建工具说明.txt") , "file",null);
        Response upload = jfunc.upload(formFile);
        System.out.println(upload);
    }
    @Test
    public void testUploadWithParam(){
        FormFile formFile = new FormFile(new File("F:\\xiongshiyan\\material\\构建工具\\Grunt\\code\\自动化构建工具说明.txt") , "file",null);
        Response upload = jfunc.uploadWithParam("xxxxxxx" , 12 , formFile);
        System.out.println(upload);
    }
}
