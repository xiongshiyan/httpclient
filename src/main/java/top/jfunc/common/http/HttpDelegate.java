package top.jfunc.common.http;

import top.jfunc.common.http.smart.NativeSmartHttpClient;
import top.jfunc.common.http.smart.SmartHttpClient;
import top.jfunc.common.utils.ClassUtil;

/**
 * 动态获取SmartHttpClient的实现类，基于jar包检测 <pre>HttpUtil.delegate()....</pre>
 * @author xiongshiyan at 2017/12/11
 */
public class HttpDelegate {
    private HttpDelegate(){}

    /**
     * http请求工具代理对象
     */
    private static final SmartHttpClient delegate;
    public static SmartHttpClient delegate() {
        return delegate;
    }

    static {

        //根据类路径的jar加载默认顺序是 OKHttp3、ApacheHttpClient、URLConnection
        SmartHttpClient delegateToUse = null;
        // okhttp3.OkHttpClient ?
        if (ClassUtil.isPresent(HttpDelegate.class.getClassLoader() ,"okhttp3.OkHttpClient" , "okio.Okio")) {
            delegateToUse = new top.jfunc.common.http.smart.OkHttp3SmartHttpClient();
        }
        // org.apache.http.impl.client.CloseableHttpClient ?
        else if (ClassUtil.isPresent(HttpDelegate.class.getClassLoader() ,
                "org.apache.http.impl.client.CloseableHttpClient","org.apache.http.impl.client.HttpClientBuilder")) {
            delegateToUse = new top.jfunc.common.http.smart.ApacheSmartHttpClient();
        }
        // java.net.URLConnection
        else if (ClassUtil.isPresent(HttpDelegate.class.getClassLoader() ,"java.net.URLConnection")) {
            delegateToUse = new NativeSmartHttpClient();
        }
        delegate = delegateToUse;
    }
}
