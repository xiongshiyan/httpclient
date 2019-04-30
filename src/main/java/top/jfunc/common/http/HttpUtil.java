package top.jfunc.common.http;

import top.jfunc.common.http.basic.FormFile;
import top.jfunc.common.http.basic.HttpClient;
import top.jfunc.common.http.basic.NativeHttpClient;
import top.jfunc.common.http.smart.NativeSmartHttpClient;
import top.jfunc.common.http.smart.Request;
import top.jfunc.common.http.smart.Response;
import top.jfunc.common.http.smart.SmartHttpClient;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.ClassUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 1.静态初始化SmartHttpClient，默认采用基于HttpUrlConnection的
 * 2.动态获取SmartHttpClient的实现类，基于jar包检测 <pre>HttpUtil.delegate()....</pre>
 * 3.提供对SmartHttpClient的静态代理，使可以一句话实现Http请求
 * @author xiongshiyan at 2017/12/11
 */
public class HttpUtil {
    private HttpUtil(){}

    /**
     * 废弃了，请使用功能更强的SmartHttpClient
     * @see SmartHttpClient
     */
    @Deprecated
    private static HttpClient BASIC_HTTP_CLIENT = new NativeHttpClient();
    @Deprecated
    public static HttpClient getBasicHttpClient() {
        return BASIC_HTTP_CLIENT;
    }
    @Deprecated
    public static void setBasicHttpClient(HttpClient httpClient){BASIC_HTTP_CLIENT = httpClient;}


    private static SmartHttpClient SMART_HTTP_CLIENT = new NativeSmartHttpClient();
    public static SmartHttpClient getSmartHttpClient() { return SMART_HTTP_CLIENT; }
    public static void setSmartHttpClient(SmartHttpClient smartHttpClient){SMART_HTTP_CLIENT = smartHttpClient;}


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
        if (ClassUtil.isPresent(HttpUtil.class.getClassLoader() ,"okhttp3.OkHttpClient" , "okio.Okio")) {
            delegateToUse = new top.jfunc.common.http.smart.OkHttp3SmartHttpClient();
        }
        // org.apache.http.impl.client.CloseableHttpClient ?
        else if (ClassUtil.isPresent(HttpUtil.class.getClassLoader() ,
                "org.apache.http.impl.client.CloseableHttpClient","org.apache.http.impl.client.HttpClientBuilder")) {
            delegateToUse = new top.jfunc.common.http.smart.ApacheSmartHttpClient();
        }
        // java.net.URLConnection
        else if (ClassUtil.isPresent(HttpUtil.class.getClassLoader() ,"java.net.URLConnection")) {
            delegateToUse = new top.jfunc.common.http.smart.NativeSmartHttpClient();
        }
        delegate = delegateToUse;
    }



    /**
     * 以下代码是对SmartHttpClient的代理，使用更方便
     * <pre>
     *     HttpUtil.get(...)
     *     HttpUtil.post(...)
     *     HttpUtil.getAsBytes(...)
     *     HttpUtil.getAsFile(...) 文件下载
     *     HttpUtil.upload(...) 文件上传
     * </pre>
     * @since 1.0.1
     */
    /*****************************************Proxy the method of SmartHttpClient interface*************************************************/

    public static Response get(Request request) throws IOException{
        return getSmartHttpClient().get(request);
    }
    public static Response post(Request request) throws IOException{
        return getSmartHttpClient().post(request);
    }
    public static byte[] getAsBytes(Request request) throws IOException{
        return getSmartHttpClient().getAsBytes(request);
    }
    public static File getAsFile(Request request) throws IOException{
        return getSmartHttpClient().getAsFile(request);
    }
    public static Response upload(Request request) throws IOException{
        return getSmartHttpClient().upload(request);
    }
    /*****************************************Proxy the method of HttpClient interface*************************************************/
    public static String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException{
        return getSmartHttpClient().get(url, params, headers, connectTimeout, readTimeout, resultCharset);
    }
    public static String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getSmartHttpClient().get(url, params, headers, connectTimeout, readTimeout);
    }
    public static String get(String url, Map<String, String> params, Map<String, String> headers, String resultCharset) throws IOException{
        return getSmartHttpClient().get(url,params,headers,resultCharset);
    }
    public static String get(String url, Map<String, String> params, Map<String, String> headers) throws IOException{
        return getSmartHttpClient().get(url,params,headers);
    }
    public static String get(String url, Map<String, String> params, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException{
        return getSmartHttpClient().get(url,params,connectTimeout,readTimeout,resultCharset);
    }
    public static String get(String url, Map<String, String> params, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getSmartHttpClient().get(url,params,connectTimeout,readTimeout);
    }
    public static String get(String url, Map<String, String> params, String resultCharset) throws IOException{
        return getSmartHttpClient().get(url,params,resultCharset);
    }
    public static String get(String url, Map<String, String> params) throws IOException{
        return getSmartHttpClient().get(url,params);
    }
    public static String get(String url, String resultCharset) throws IOException{
        return getSmartHttpClient().get(url,resultCharset);
    }
    public static String get(String url) throws IOException{
        return getSmartHttpClient().get(url);
    }

    public static String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException{
        return getSmartHttpClient().post(url, body, contentType, headers, connectTimeout, readTimeout, bodyCharset, resultCharset);
    }
    public static String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getSmartHttpClient().post(url,body,contentType,headers,connectTimeout,readTimeout);
    }
    public static String post(String url, String body, String contentType, Map<String, String> headers, String bodyCharset, String resultCharset) throws IOException{
        return getSmartHttpClient().post(url,body,contentType,headers,bodyCharset,resultCharset);
    }
    public static String post(String url, String body, String contentType, Map<String, String> headers) throws IOException{
        return getSmartHttpClient().post(url, body, contentType, headers);
    }
    public static String post(String url, String body, String contentType, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException{
        return getSmartHttpClient().post(url,body,contentType,connectTimeout,readTimeout,bodyCharset,resultCharset);
    }
    public static String post(String url, String body, String contentType, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getSmartHttpClient().post(url,body,contentType,connectTimeout,readTimeout);
    }
    public static String post(String url, String body, String contentType, String bodyCharset, String resultCharset) throws IOException{
        return getSmartHttpClient().post(url,body,contentType,bodyCharset,resultCharset);
    }
    public static String post(String url, String body, String contentType) throws IOException{
        return getSmartHttpClient().post(url,body,contentType);
    }
    public static String postJson(String url, String body, String bodyCharset, String resultCharset) throws IOException{
        return getSmartHttpClient().postJson(url,body,bodyCharset,resultCharset);
    }
    public static String postJson(String url, String body) throws IOException{
        return getSmartHttpClient().postJson(url,body);
    }
    public static String post(String url, Map<String, String> params, Map<String, String> headers, String bodyCharset, String resultCharset) throws IOException{
        return getSmartHttpClient().post(url, params , headers ,bodyCharset , resultCharset);
    }
    public static String post(String url, Map<String, String> params, Map<String, String> headers) throws IOException{
        return getSmartHttpClient().post(url, params,headers);
    }
    public static String post(String url, Map<String, String> params, String bodyCharset, String resultCharset) throws IOException{
        return getSmartHttpClient().post(url, params, bodyCharset, resultCharset);
    }
    public static String post(String url, Map<String, String> params) throws IOException{
        return getSmartHttpClient().post(url, params);
    }

    public static byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getSmartHttpClient().getAsBytes(url, headers, connectTimeout, readTimeout);
    }
    public static byte[] getAsBytes(String url, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getSmartHttpClient().getAsBytes(url, connectTimeout , readTimeout);
    }
    public static byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers) throws IOException{
        return getSmartHttpClient().getAsBytes(url , headers);
    }
    public static byte[] getAsBytes(String url) throws IOException{
        return getSmartHttpClient().getAsBytes(url);
    }

    public static File getAsFile(String url, ArrayListMultimap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getSmartHttpClient().getAsFile(url, headers, file, connectTimeout, readTimeout);
    }
    public static File getAsFile(String url, File file, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getSmartHttpClient().getAsFile(url, file , connectTimeout , readTimeout);
    }
    public static File getAsFile(String url, ArrayListMultimap<String, String> headers, File file) throws IOException{
        return getSmartHttpClient().getAsFile(url, headers  , file);
    }
    public static File getAsFile(String url, File file) throws IOException{
        return getSmartHttpClient().getAsFile(url, file);
    }

    public static String upload(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException{
        return getSmartHttpClient().upload(url, headers, connectTimeout, readTimeout, resultCharset, files);
    }
    public static String upload(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, FormFile... files) throws IOException{
        return getSmartHttpClient().upload(url, headers ,connectTimeout , readTimeout , files);
    }
    public static String upload(String url, ArrayListMultimap<String, String> headers, FormFile... files) throws IOException{
        return getSmartHttpClient().upload(url, headers ,files);
    }
    public static String upload(String url, Integer connectTimeout, Integer readTimeout, FormFile... files) throws IOException{
        return getSmartHttpClient().upload(url,connectTimeout , readTimeout , files);
    }
    public static String upload(String url, FormFile... files) throws IOException{
        return getSmartHttpClient().upload(url, files);
    }

    public static String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException{
        return getSmartHttpClient().upload(url, params, headers, connectTimeout, readTimeout, resultCharset, files);
    }
    public static String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, FormFile... files) throws IOException{
        return getSmartHttpClient().upload(url, params ,headers ,connectTimeout , readTimeout, files);
    }
    public static String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, FormFile... files) throws IOException{
        return getSmartHttpClient().upload(url, params ,headers , files);
    }
    public static String upload(String url, Integer connectTimeout, Integer readTimeout, ArrayListMultimap<String, String> params, FormFile... files) throws IOException{
        return getSmartHttpClient().upload(url, params,connectTimeout , readTimeout, files);
    }
    public static String upload(String url, Map<String, String> params, FormFile... files) throws IOException{
        return getSmartHttpClient().upload(url, params , files);
    }
}
