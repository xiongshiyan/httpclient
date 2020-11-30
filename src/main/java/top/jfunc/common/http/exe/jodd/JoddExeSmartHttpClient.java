package top.jfunc.common.http.exe.jodd;

import jodd.http.HttpRequest;
import top.jfunc.common.http.component.jodd.DefaultJoddBodyContentCallbackCreator;
import top.jfunc.common.http.component.jodd.DefaultJoddUploadContentCallbackCreator;
import top.jfunc.common.http.exe.BaseExeSmartHttpClient;

/**
 * 使用Jodd实现的Http请求类
 * @since 1.2.12
 * @since 2020.12.01
 * @author 熊诗言2020/12/01
 */
public class JoddExeSmartHttpClient extends BaseExeSmartHttpClient<HttpRequest> {
    @Override
    protected void init() {
        super.init();

        setBodyContentCallbackCreator(new DefaultJoddBodyContentCallbackCreator());
        setUploadContentCallbackCreator(new DefaultJoddUploadContentCallbackCreator());

        setHttpRequestExecutor(new JoddHttpRequestExecutor());
    }

    @Override
    public String toString() {
        return "SmartHttpClient implemented by Jodd-Http";
    }
}
