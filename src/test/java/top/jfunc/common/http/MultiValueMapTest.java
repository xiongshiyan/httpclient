package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.LinkedMultiValueMap;
import top.jfunc.common.utils.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;

/**
 * @author xiongshiyan at 2019/3/28 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class MultiValueMapTest {

    @Test
    public void testConcatParam(){
        ArrayListMultimap<String , String> multimap = new ArrayListMultimap<>();
        multimap.put("xx" , "xx");
        multimap.put("yy" , "yy");
        multimap.put("xx" , "zz");
        String contactMap = ParamUtil.contactMap(multimap);
        Assert.assertThat(contactMap , is("xx=xx&xx=zz&yy=yy"));
        Assert.assertThat(ParamUtil.contactMap(ArrayListMultiValueMap.fromMap(multimap)) , is("xx=xx&xx=zz&yy=yy"));
    }
    @Test
    public void testConcatParam2(){
        Map<String , String> map = new HashMap<>();
        map.put("xx" , "xx");
        map.put("yy" , "yy");
        String contactMap = ParamUtil.contactMap(map);
        Assert.assertThat(contactMap , is("xx=xx&yy=yy"));
        Assert.assertThat(ParamUtil.contactMap(ArrayListMultiValueMap.fromMap(map)) , is("xx=xx&yy=yy"));
    }
    @Test
    public void testConcatParam3(){
        MultiValueMap<String , String> map = new LinkedMultiValueMap<>();
        map.add("xx" , "xx");
        map.add("yy" , "yy");
        String contactMap = ParamUtil.contactMap(map);
        Assert.assertThat(contactMap , is("xx=xx&yy=yy"));
        Assert.assertThat(ParamUtil.contactMap(ArrayListMultimap.fromMap(map)) , is("xx=xx&yy=yy"));
    }
    @Test
    public void testConcatParam4(){
        MultiValueMap<String , String> map = new ArrayListMultiValueMap<>();
        map.add("xx" , "xx");
        map.add("yy" , "yy");
        String contactMap = ParamUtil.contactMap(map);
        Assert.assertThat(contactMap , is("xx=xx&yy=yy"));
        Assert.assertThat(ParamUtil.contactMap(ArrayListMultimap.fromMap(map)) , is("xx=xx&yy=yy"));
    }
    @Test
    public void testInit(){
        MultiValueMap<String, String> multiValueMap =
                new ArrayListMultiValueMap<String, String>(){
            {
                add("charset" , "utf-8");
                add("charset2" , "gbk");
            }
        };
        Assert.assertThat(multiValueMap.size() , is(2));
        Assert.assertThat(multiValueMap.getFirst("charset") , is("utf-8"));
        Assert.assertThat(multiValueMap.getLast("charset2") , is("gbk"));
    }
}
