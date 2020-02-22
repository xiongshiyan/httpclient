package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.basic.CommonRequest;

public class BooleanTest {
    @Test
    public void testBoolean(){
        CommonRequest request = CommonRequest.of();
        Config config = Config.defaultConfig();
        //Request未指定的时候使用Config的
        Assert.assertFalse(config.retainResponseHeadersWithDefault(request.retainResponseHeaders()));

        //request指定了使用Request的
        request.retainResponseHeaders(HttpRequest.RETAIN_RESPONSE_HEADERS);
        Assert.assertTrue(config.retainResponseHeadersWithDefault(request.retainResponseHeaders()));

        //Request未指定的时候使用Config的，Config改变过的
        config.retainResponseHeaders(HttpRequest.RETAIN_RESPONSE_HEADERS);
        CommonRequest commonRequest = CommonRequest.of();
        Assert.assertTrue(config.retainResponseHeadersWithDefault(commonRequest.retainResponseHeaders()));
    }
}
