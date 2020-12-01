package top.jfunc.common.http.smart;

import org.apache.http.HttpEntityEnclosingRequest;
import top.jfunc.common.http.component.apache.DefaultApacheBodyContentCallbackCreator;
import top.jfunc.common.http.component.apache.DefaultApacheUploadContentCallbackCreator;
import top.jfunc.common.http.component.apache.ApacheHttpRequestExecutor;

/**
 * 使用Apache HttpClient 实现的Http请求类
 * @since 1.2.12
 * @since 2020.12.01
 * @author 熊诗言2020/12/01
 */
public class ApacheSmartHttpClient extends BaseExeSmartHttpClient<HttpEntityEnclosingRequest> {
    @Override
    protected void init() {
        super.init();

        setBodyContentCallbackCreator(new DefaultApacheBodyContentCallbackCreator());
        setUploadContentCallbackCreator(new DefaultApacheUploadContentCallbackCreator());

        setHttpRequestExecutor(new ApacheHttpRequestExecutor());
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by Apache's httpcomponents";
    }
}
