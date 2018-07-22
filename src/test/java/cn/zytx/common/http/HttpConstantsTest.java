package cn.zytx.common.http;

import org.junit.Assert;
import org.junit.Test;

public class HttpConstantsTest {
    @Test
    public void testInit(){
        Assert.assertEquals(15000 , HttpConstants.DEFAULT_CONNECT_TIMEOUT);
        Assert.assertEquals(15000 , HttpConstants.DEFAULT_READ_TIMEOUT);
        Assert.assertEquals("UTF-8" , HttpConstants.DEFAULT_CHARSET);
    }

    @Test
    public void testSetProp(){
        System.setProperty("DEFAULT_CONNECT_TIMEOUT" , "20000");
        Assert.assertEquals(20000 , HttpConstants.DEFAULT_CONNECT_TIMEOUT);
        Assert.assertEquals(15000 , HttpConstants.DEFAULT_READ_TIMEOUT);
    }
    @Test
    public void testSetD(){
        //-D转入
        Assert.assertEquals(15000 , HttpConstants.DEFAULT_CONNECT_TIMEOUT);
        Assert.assertEquals(20000 , HttpConstants.DEFAULT_READ_TIMEOUT);
    }
}
