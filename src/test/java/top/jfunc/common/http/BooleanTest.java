package top.jfunc.common.http;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.basic.CommonRequest;
import top.jfunc.common.utils.ObjectUtil;

public class BooleanTest {
    @Test
    public void testBoolean(){
        CommonRequest request = CommonRequest.of();
        Config config = Config.defaultConfig();
        //Request未指定的时候使用Config的
        Assert.assertFalse(ObjectUtil.defaultIfNull(request.retainResponseHeaders() , config.retainResponseHeaders()));

        //request指定了使用Request的
        request.retainResponseHeaders(Config.RETAIN_RESPONSE_HEADERS);
        Assert.assertTrue(ObjectUtil.defaultIfNull(request.retainResponseHeaders() , config.retainResponseHeaders()));

        //Request未指定的时候使用Config的，Config改变过的
        config.retainResponseHeaders(Config.RETAIN_RESPONSE_HEADERS);
        CommonRequest commonRequest = CommonRequest.of();
        Assert.assertTrue(ObjectUtil.defaultIfNull(commonRequest.retainResponseHeaders() , config.retainResponseHeaders()));
    }
}
