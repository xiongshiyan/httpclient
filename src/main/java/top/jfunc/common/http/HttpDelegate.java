package top.jfunc.common.http;

import top.jfunc.common.http.smart.SmartHttpClient;
import top.jfunc.common.utils.ClassUtil;

/**
 * 动态获取SmartHttpClient的实现类，基于jar包检测 <pre>HttpStatic.delegate()....</pre>
 * @author xiongshiyan at 2017/12/11
 */
public class HttpDelegate {
    private HttpDelegate(){}

    /**
     * http请求工具代理对象
     */
    private static final SmartHttpClient DELEGATE = initDelegate();
    public static SmartHttpClient delegate() {
        return DELEGATE;
    }

    private static SmartHttpClient initDelegate() {
        //根据类路径的jar加载默认顺序是 OKHttp3、ApacheHttpClient、URLConnection
        SmartHttpClient delegateToUse = null;
        // okhttp3.OkHttpClient ?
        if (ClassUtil.isPresent(HttpDelegate.class.getClassLoader() ,
                "top.jfunc.common.http.smart.OkHttp3SmartHttpClient")) {
            delegateToUse = new top.jfunc.common.http.smart.OkHttp3SmartHttpClient();
        }
        // org.apache.http.impl.client.CloseableHttpClient ?
        else if (ClassUtil.isPresent(HttpDelegate.class.getClassLoader() ,
                "top.jfunc.common.http.smart.ApacheSmartHttpClient")) {
            delegateToUse = new top.jfunc.common.http.smart.ApacheSmartHttpClient();
        }
        // jodd.http.HttpRequest ?
        else if (ClassUtil.isPresent(HttpDelegate.class.getClassLoader() ,
                "top.jfunc.common.http.smart.JoddSmartHttpClient")) {
            delegateToUse = new top.jfunc.common.http.smart.JoddSmartHttpClient();
        }
        // java.net.URLConnection
        else if (ClassUtil.isPresent(HttpDelegate.class.getClassLoader() ,
              "top.jfunc.common.http.smart.NativeSmartHttpClient")) {
            delegateToUse = new top.jfunc.common.http.smart.NativeSmartHttpClient();
        }
        return delegateToUse;
    }
}
