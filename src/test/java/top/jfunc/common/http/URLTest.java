package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.Matchers.is;

/**
 * @author xiongshiyan at 2019/5/20 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class URLTest {

    @Test
    public void testURL() throws Exception{
        URL url = new URL("http://www.runoob.com/index.html?language=cn#j2se");
        Assert.assertThat(url.toString() , is("http://www.runoob.com/index.html?language=cn#j2se"));
        Assert.assertThat(url.getProtocol() , is("http"));
        Assert.assertThat(url.getAuthority() , is("www.runoob.com"));
        Assert.assertThat(url.getFile() , is("/index.html?language=cn"));
        Assert.assertThat(url.getHost() , is("www.runoob.com"));
        Assert.assertThat(url.getPath() , is("/index.html"));
        Assert.assertThat(url.getPort() , is(-1));
        Assert.assertThat(url.getDefaultPort() , is(80));
        Assert.assertThat(url.getQuery() , is("language=cn"));
        Assert.assertThat(url.getRef() , is("j2se"));
    }
}
