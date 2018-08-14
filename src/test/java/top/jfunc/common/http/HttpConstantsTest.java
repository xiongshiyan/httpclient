package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class HttpConstantsTest {
    @Test
    public void testInit(){
        Assert.assertEquals(15000 , HttpConstants.DEFAULT_CONNECT_TIMEOUT);
        Assert.assertEquals(15000 , HttpConstants.DEFAULT_READ_TIMEOUT);
        Assert.assertEquals("UTF-8" , HttpConstants.DEFAULT_CHARSET);
    }

    @Test
    @Ignore
    public void testSetProp(){
        System.setProperty("DEFAULT_CONNECT_TIMEOUT" , "20000");
        Assert.assertEquals(20000 , HttpConstants.DEFAULT_CONNECT_TIMEOUT);
        Assert.assertEquals(15000 , HttpConstants.DEFAULT_READ_TIMEOUT);
    }
    @Test
    @Ignore
    public void testSetD(){
        //-D转入
        Assert.assertEquals(15000 , HttpConstants.DEFAULT_CONNECT_TIMEOUT);
        Assert.assertEquals(20000 , HttpConstants.DEFAULT_READ_TIMEOUT);
    }
}
