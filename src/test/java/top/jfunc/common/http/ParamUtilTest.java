package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.http.holder.DefaultRouteParamHolder;
import top.jfunc.common.http.holder.RouteParamHolder;
import top.jfunc.common.http.util.ParamUtil;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.LinkedMultiValueMap;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.RadixUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;

/**
 * @author xiongshiyan at 2019/3/28 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ParamUtilTest {
    private String completedUrlHttp = "http://localhost:8080/ssss";
    private String completedUrlHttps = "https://localhost:8080/ssss";
    @Test
    public void testIsHttpIsHttps(){
        Assert.assertTrue(ParamUtil.isHttps(completedUrlHttps));
        Assert.assertTrue(ParamUtil.isHttp(completedUrlHttp));
        Assert.assertFalse(ParamUtil.isHttp(completedUrlHttps));
        Assert.assertFalse(ParamUtil.isHttps(completedUrlHttp));

        Assert.assertFalse(ParamUtil.isHttps("/ssss"));

        Assert.assertFalse(ParamUtil.isCompletedUrl("/ssss"));
        Assert.assertTrue(ParamUtil.isCompletedUrl(completedUrlHttps));
        Assert.assertTrue(ParamUtil.isCompletedUrl(completedUrlHttp));
    }

    @Test
    public void testAddBaseUrlIfNecessary(){
        Assert.assertEquals(completedUrlHttps ,
                ParamUtil.concatUrlIfNecessary(null , completedUrlHttps));
        Assert.assertEquals(completedUrlHttps ,
                ParamUtil.concatUrlIfNecessary("https://localhost:8080" , completedUrlHttps));
        Assert.assertEquals(completedUrlHttps ,
                ParamUtil.concatUrlIfNecessary("https://localhost:8080/" , "/ssss"));
        Assert.assertEquals(completedUrlHttps ,
                ParamUtil.concatUrlIfNecessary("https://localhost:8080" , "ssss"));
        Assert.assertEquals(completedUrlHttps ,
                ParamUtil.concatUrlIfNecessary("https://localhost:8080/" , "ssss"));
        Assert.assertEquals(completedUrlHttps ,
                ParamUtil.concatUrlIfNecessary("https://localhost:8080" , "/ssss"));
    }

    @Test
    public void testConcatParam2(){
        Map<String , String> map = new HashMap<>();
        map.put("xx" , "xx");
        map.put("yy" , "yy");
        String contactMap = ParamUtil.contactMap(map);
        Assert.assertThat(contactMap , is("xx=xx&yy=yy"));
    }
    @Test
    public void testConcatParamCharsetUTF8(){
        Map<String , String> map = new HashMap<>();
        map.put("xx" , "熊诗言");
        map.put("yy" , "yy");
        String contactMap = ParamUtil.contactMap(map);
        Assert.assertThat(contactMap , is("xx=%E7%86%8A%E8%AF%97%E8%A8%80&yy=yy"));
    }
    @Test
    public void testConcatParamCharsetGBK() throws Exception{
        Map<String , String> map = new HashMap<>();
        map.put("xx" , "熊诗言");
        map.put("yy" , "yy");
        String contactMap = ParamUtil.contactMap(map , "GBK");
        Assert.assertThat(contactMap , is("xx=%D0%DC%CA%AB%D1%D4&yy=yy"));

        byte[] gbks = "熊诗言".getBytes("GBK");
        for (byte gbk : gbks) {
            System.out.println(gbk);
        }
        System.out.println(RadixUtil.toHex(gbks));
    }
    @Test
    public void testConcatParam3(){
        MultiValueMap<String , String> map = new LinkedMultiValueMap<>();
        map.add("xx" , "xx");
        map.add("yy" , "yy");
        String contactMap = ParamUtil.contactMap(map);
        Assert.assertThat(contactMap , is("xx=xx&yy=yy"));
    }
    @Test
    public void testConcatParam4(){
        MultiValueMap<String , String> map = new ArrayListMultiValueMap<>();
        map.add("xx" , "xx");
        map.add("yy" , "yy");
        String contactMap = ParamUtil.contactMap(map);
        Assert.assertThat(contactMap , is("xx=xx&yy=yy"));
    }
    @Test
    public void testConcatParam5(){
        MultiValueMap<String , String> map = new ArrayListMultiValueMap<>();
        map.add("xx" , "xx");
        map.add("yy" , "熊诗言");
        Set<Map.Entry<String, List<String>>> entries = map.entrySet();
        String contactMap = ParamUtil.contactIterable(entries , "UTF-8");
        Assert.assertThat(contactMap , is("xx=xx&yy=%E7%86%8A%E8%AF%97%E8%A8%80"));

        String s = ParamUtil.contactIterableNotEncode(entries);
        Assert.assertThat(s , is("xx=xx&yy=熊诗言"));
    }

    @Test
    public void testReplaceRoute(){
        String url = "http://httpbin.org/book/{id}/{do}/{gg}";
        Map<String , String> routes = new HashMap<>();
        routes.put("id" , "121313");
        routes.put("id2" , "12222221313");
        routes.put("do" , "edit");
        routes.put("gg" , "gg");
        String necessary = ParamUtil.replaceRouteParamsIfNecessary(url, routes);
        Assert.assertEquals("http://httpbin.org/book/121313/edit/gg" , necessary);
    }

    @Test
    public void testReplaceRoute2(){
        String url = "http://httpbin.org/book/{id}/{do}/{gg}/{1}/{2}";
        RouteParamHolder routeParamHolder = new DefaultRouteParamHolder();
        routeParamHolder.addRouteParam("id" , "121313");
        routeParamHolder.addRouteParam("id2" , "12222221313");
        routeParamHolder.addRouteParam("do" , "edit");

        //key-value用分号隔开的
        routeParamHolder.addRouteParams("gg:gg");

        //从1开始的顺序参数
        routeParamHolder.addOrderedRouteParams("xxxxx" , "yyyyy");

        String necessary = ParamUtil.replaceRouteParamsIfNecessary(url, routeParamHolder.getMap());
        Assert.assertEquals("http://httpbin.org/book/121313/edit/gg/xxxxx/yyyyy" , necessary);
    }
    @Test
    public void testReplaceRoute3(){
        String url = "http://httpbin.org/book/{id}/{do}/{gg}/{0}/{1}";
        RouteParamHolder routeParamHolder = new DefaultRouteParamHolder(){
            @Override
            public int orderedParamsFrom() {
                return 0;
            }
        };
        routeParamHolder.addRouteParam("id" , "121313");
        routeParamHolder.addRouteParam("id2" , "12222221313");
        routeParamHolder.addRouteParam("do" , "edit");

        //key-value用分号隔开的
        routeParamHolder.addRouteParams("gg:gg");

        //从1开始的顺序参数
        routeParamHolder.addOrderedRouteParams("xxxxx" , "yyyyy");

        String necessary = ParamUtil.replaceRouteParamsIfNecessary(url, routeParamHolder.getMap());
        Assert.assertEquals("http://httpbin.org/book/121313/edit/gg/xxxxx/yyyyy" , necessary);
    }

    @Test
    public void testParseMap1() {
        String kv = "key1=value1&key2=value2";
        Map<String, String> map1 = ParamUtil.parseParam(kv);
        Assert.assertEquals(2 , map1.size());
        Assert.assertEquals("value1" , map1.get("key1"));
        Assert.assertEquals("value2" , map1.get("key2"));


        kv = "xx";
        Map<String, String> map2 = ParamUtil.parseParam(kv);
        Assert.assertEquals(1 , map2.size());
        Assert.assertEquals("" , map2.get("xx"));

        kv = "xx=";
        Map<String, String> map3 = ParamUtil.parseParam(kv);
        Assert.assertEquals(1 , map3.size());
        Assert.assertEquals("" , map3.get("xx"));

        kv = "xx=&x1=";
        Map<String, String> map4 = ParamUtil.parseParam(kv);
        Assert.assertEquals(2 , map4.size());
        Assert.assertEquals("" , map4.get("xx"));
        Assert.assertEquals("" , map4.get("x1"));

        kv = "xx&x1=&x2=ss";
        Map<String, String> map5 = ParamUtil.parseParam(kv);
        Assert.assertEquals(3 , map5.size());
        Assert.assertEquals("" , map5.get("xx"));
        Assert.assertEquals("" , map5.get("x1"));
        Assert.assertEquals("ss" , map5.get("x2"));
    }
    @Test
    public void testParseMap2() {
        Map<String , String> map = new HashMap<>(2);
        map.put("key1" , "熊诗言");
        map.put("key2" , "value2");
        String s = ParamUtil.contactMap(map);
        //key1=%E7%86%8A%E8%AF%97%E8%A8%80&key2=value2

        Map<String, String> map1 = ParamUtil.parseParam(s , (v)->v);
        Assert.assertEquals(2 , map1.size());
        Assert.assertEquals("%E7%86%8A%E8%AF%97%E8%A8%80" , map1.get("key1"));
        Assert.assertEquals("value2" , map1.get("key2"));

        Map<String, String> map2 = ParamUtil.parseParam(s , ParamUtil::urlDecode);
        Assert.assertEquals(2 , map2.size());
        Assert.assertEquals("熊诗言" , map2.get("key1"));
        Assert.assertEquals("value2" , map2.get("key2"));

        Map<String, String> map3 = ParamUtil.parseParam("key1=%E7%86%8A%E8%AF%97%E8%A8%80|key2=value2", ParamUtil::urlDecode, "\\|");
        Assert.assertEquals(2 , map3.size());
        Assert.assertEquals("熊诗言" , map3.get("key1"));
        Assert.assertEquals("value2" , map3.get("key2"));
        MultiValueMap<String, String> map4 = ParamUtil.parseMultiValueParam("key1=%E7%86%8A%E8%AF%97%E8%A8%80|key2=value2", ParamUtil::urlDecode, "\\|");
        Assert.assertEquals(2 , map4.size());
        Assert.assertEquals("熊诗言" , map4.get("key1").get(0));
        Assert.assertEquals("value2" , map4.get("key2").get(0));

    }
}
