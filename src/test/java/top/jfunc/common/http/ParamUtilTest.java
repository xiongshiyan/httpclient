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
}
