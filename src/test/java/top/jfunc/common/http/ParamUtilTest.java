package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.LinkedMultiValueMap;
import top.jfunc.common.utils.MultiValueMap;

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
                ParamUtil.addBaseUrlIfNecessary(null , completedUrlHttps));
        Assert.assertEquals(completedUrlHttps ,
                ParamUtil.addBaseUrlIfNecessary("https://localhost:8080" , completedUrlHttps));
        Assert.assertEquals(completedUrlHttps ,
                ParamUtil.addBaseUrlIfNecessary("https://localhost:8080/" , "/ssss"));
        Assert.assertEquals(completedUrlHttps ,
                ParamUtil.addBaseUrlIfNecessary("https://localhost:8080" , "ssss"));
        Assert.assertEquals(completedUrlHttps ,
                ParamUtil.addBaseUrlIfNecessary("https://localhost:8080/" , "ssss"));
        Assert.assertEquals(completedUrlHttps ,
                ParamUtil.addBaseUrlIfNecessary("https://localhost:8080" , "/ssss"));
    }

    @Test
    public void testConcatParam(){
        ArrayListMultimap<String , String> multimap = new ArrayListMultimap<>();
        multimap.put("xx" , "xx");
        multimap.put("yy" , "yy");
        multimap.put("xx" , "zz");
        String contactMap = ParamUtil.contactMap(multimap);
        Assert.assertThat(contactMap , is("xx=xx&xx=zz&yy=yy"));
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
        map.add("yy" , "yy");
        Set<Map.Entry<String, List<String>>> entries = map.entrySet();
        String contactMap = ParamUtil.contactIterable(entries , "UTF-8");
        Assert.assertThat(contactMap , is("xx=xx&yy=yy"));
    }
    @Test
    public void testMergeMultiMap(){
        MultiValueMap<String , String> first = new ArrayListMultiValueMap<>();
        first.add("xx" , "xx");
        first.add("yy" , "yy");
        MultiValueMap<String , String> second = new ArrayListMultiValueMap<>();
        second.add("xx" , "xx");
        second.add("zz" , "zz");

        MultiValueMap<String, String> mergeMap = ParamUtil.mergeMap(first, second);
        Assert.assertThat(mergeMap.size() , is(3));
        Assert.assertThat(mergeMap.toString() , is("{xx=[xx, xx], yy=[yy], zz=[zz]}"));
    }
    @Test
    public void testMergeMap1(){
        Map<String , String> first = new HashMap<>();
        first.put("xx" , "xx");
        first.put("yy" , "yy");
        Map<String , String> second = new HashMap<>();
        second.put("xx" , "cc");
        second.put("zz" , "zz");

        Map<String, String> mergeMap = ParamUtil.mergeMap(first, second , true);
        Assert.assertThat(mergeMap.size() , is(3));
        Assert.assertThat(mergeMap.get("xx") , is("xx"));
    }
    @Test
    public void testMergeMap2(){
        Map<String , String> first = new HashMap<>();
        first.put("xx" , "xx");
        first.put("yy" , "yy");
        Map<String , String> second = new HashMap<>();
        second.put("xx" , "cc");
        second.put("zz" , "zz");
        Map<String, String> mergeMap = ParamUtil.mergeMap(first, second , false);
        Assert.assertThat(mergeMap.size() , is(3));
        Assert.assertThat(mergeMap.get("xx") , is("cc"));
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
}
