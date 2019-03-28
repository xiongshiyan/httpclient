package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author xiongshiyan at 2019/3/28 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ParamUtilTest {
    @Test
    public void testIsHttpIsHttps(){
        Assert.assertTrue(ParamUtil.isHttps("https://localhost:8080/ssss"));
        Assert.assertTrue(ParamUtil.isHttp("http://localhost:8080/ssss"));
        Assert.assertFalse(ParamUtil.isHttp("https://localhost:8080/ssss"));
        Assert.assertFalse(ParamUtil.isHttps("http://localhost:8080/ssss"));

        Assert.assertFalse(ParamUtil.isHttps("/ssss"));

        Assert.assertFalse(ParamUtil.isCompletedUrl("/ssss"));
        Assert.assertTrue(ParamUtil.isCompletedUrl("https://localhost:8080/ssss"));
        Assert.assertTrue(ParamUtil.isCompletedUrl("http://localhost:8080/ssss"));
    }

    @Test
    public void testAddBaseUrlIfNecessary(){
        Assert.assertEquals("https://localhost:8080/ssss" ,
                ParamUtil.addBaseUrlIfNecessary(null , "https://localhost:8080/ssss"));
        Assert.assertEquals("https://localhost:8080/ssss" ,
                ParamUtil.addBaseUrlIfNecessary("https://localhost:8080" , "https://localhost:8080/ssss"));
        Assert.assertEquals("https://localhost:8080/ssss" ,
                ParamUtil.addBaseUrlIfNecessary("https://localhost:8080/" , "/ssss"));
        Assert.assertEquals("https://localhost:8080/ssss" ,
                ParamUtil.addBaseUrlIfNecessary("https://localhost:8080" , "ssss"));
        Assert.assertEquals("https://localhost:8080/ssss" ,
                ParamUtil.addBaseUrlIfNecessary("https://localhost:8080/" , "ssss"));
        Assert.assertEquals("https://localhost:8080/ssss" ,
                ParamUtil.addBaseUrlIfNecessary("https://localhost:8080" , "/ssss"));
    }
}
