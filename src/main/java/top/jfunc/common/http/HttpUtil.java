package top.jfunc.common.http;

import top.jfunc.common.http.basic.HttpClient;
import top.jfunc.common.http.basic.NativeHttpClient;
import top.jfunc.common.http.converter.json.ConverterSmartHttpClient;
import top.jfunc.common.http.smart.NativeSmartHttpClient;
import top.jfunc.common.http.smart.SmartHttpClient;
import top.jfunc.common.utils.ClassUtil;

/**
 * @author xiongshiyan at 2017/12/11
 */
public class HttpUtil {
    private HttpUtil(){}

    /**
     * 静态绑定
     */
    private static HttpClient BASIC_HTTP_CLIENT = new NativeHttpClient();
    private static SmartHttpClient SMART_HTTP_CLIENT = new NativeSmartHttpClient();

    public static HttpClient getBasicHttpClient() {
        return BASIC_HTTP_CLIENT;
    }
    public static void setBasicHttpClient(HttpClient httpClient){BASIC_HTTP_CLIENT = httpClient;}
    public static SmartHttpClient getSmartHttpClient() {
        return SMART_HTTP_CLIENT;
    }
    public static void setSmartHttpClient(SmartHttpClient smartHttpClient){SMART_HTTP_CLIENT = smartHttpClient;}


    // http请求工具代理对象
    private static final ConverterSmartHttpClient delegate;
    public static ConverterSmartHttpClient delegate() {
        return delegate;
    }

    static {

        //根据类路径的jar加载默认顺序是 OKHttp3、ApacheHttpClient、URLConnection
        ConverterSmartHttpClient delegateToUse = null;
        // okhttp3.OkHttpClient ?
        if (ClassUtil.isPresent(HttpUtil.class.getClassLoader() ,"okhttp3.OkHttpClient" , "okio.Okio")) {
            delegateToUse = new top.jfunc.common.http.converter.json.ConverterOkHttp3SmartHttpClient();
        }
        // org.apache.http.impl.client.CloseableHttpClient ?
        else if (ClassUtil.isPresent(HttpUtil.class.getClassLoader() ,
                "org.apache.http.impl.client.CloseableHttpClient","org.apache.http.impl.client.HttpClientBuilder")) {
            delegateToUse = new top.jfunc.common.http.converter.json.ConverterApacheSmartHttpClient();
        }
        // java.net.URLConnection
        else if (ClassUtil.isPresent(HttpUtil.class.getClassLoader() ,"java.net.URLConnection")) {
            delegateToUse = new top.jfunc.common.http.converter.json.ConverterNativeSmartHttpClient();
        }
        delegate = delegateToUse;
    }
}
