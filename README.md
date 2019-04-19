# httpclient

#### 项目介绍

http客户端接口设计，完全模拟http可能的参数，有多种实现：OKHttp3、ApacheHttpClient、HttpURLConnection，可以随意切换http实现。
我用此工具逐渐替换了业务项目工程中不统一、繁杂的各种HttpClient工具的实现、版本，只需要面向统一、抽象的操作接口。

http模块的架构设计和使用方式见CSDN博客
[一个http请求工具类的接口化（接口设计）](https://blog.csdn.net/xxssyyyyssxx/article/details/80715202)
[一个http请求工具类的接口化（多种实现）](https://blog.csdn.net/xxssyyyyssxx/article/details/80715837)


features:

* HttpClient接口体系
* SmartHttpClient（继承HttpClient）接口体系：基于Request-Response
* 支持文件上传、下载
* 支持https
* 支持基于OkHttp3、ApacheHttpClient、HttpURLConnection的切换
* HttpUtil支持根据jar包加载实现
* 配置项可以通过-D或者System.setProperty()全局设置，可以对某个实现的对象例如 `NativeSmartHttpClient` 全局设置，也可以针对某一个请求Request单独设置，优先级逐渐升高
* 支持返回值和JavaBean之间的转换，基于项目 https://gitee.com/xxssyyyyssxx/httpclient-converter
* 通过Config全局配置默认参数
* 支持全局header设置

### 使用方式

下载本项目，gradle clean build得到的jar包引入工程即可。本项目依赖于[utils](https://gitee.com/xxssyyyyssxx/utils)

lastest version:1.0

#### 1.直接导入 
compile 'top.jfunc.common:network:${version}'
#### 2.如果想只使用HttpURLConnection实现 
compile ('top.jfunc.common:network:${version}'){
        exclude group:'org.apache.httpcomponents'
        exclude group:'com.squareup.okhttp3'
        exclude group:'commons-net'
    }
#### 3.如果想只使用ApacheHttpClient实现 
compile ('top.jfunc.common:network:${version}'){
        exclude group:'com.squareup.okhttp3'
        exclude group:'commons-net'
    }
#### 4.如果想只使用Okhttp3实现 
compile ('top.jfunc.common:network:${version}'){
        exclude group:'org.apache.httpcomponents'
        exclude group:'com.squareup.okhttp3'
        exclude group:'commons-net'
    }


### 具体的使用方式：

面向SmartHttpClient，可以使用HttpUtil的delegate获取一个实现，或者自己实例化一个。在SpringBoot项目中，用Bean注入：

```
@Configuration
public class HttpConfig {
    @Bean("smartHttpClient")
    public SmartHttpClient smartHttpClient(){
        //如果要更换http的实现或者做更多的事情，可以对此bean进行配置
        NativeSmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        // new OkHttp3SmartHttpClient();
        // new ApacheSmartHttpClient(){
                //重写某些方法
        };
        smartHttpClient.setBaseUrl("....");//设置baseUrl
        retrun smartHttpClient;
    }
}
```

当拿到实例之后，就可以使用接口定义的所有的方法用于http请求。
 **HttpClient接口定义了基本的http请求方法，SmartHttpClient继承于HttpClient，新增了基于Request的方法** ：

```
public interface SmartHttpClient extends HttpClient {
    /**
     * @param config config
     */
    @Override
    SmartHttpClient setConfig(Config config);

    /**
     * GET方法
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    Response get(Request request) throws IOException;
    /**
     * POST方法
     * @param request 请求参数
     * @return 响应
     * @throws IOException 超时等IO异常
     */
    Response post(Request request) throws IOException;

    Response httpMethod(Request request, Method method) throws IOException;

    /**
     * 下载为字节数组
     * @param request 请求参数
     * @return byte[]
     * @throws IOException IOException
     */
    byte[] getAsBytes(Request request) throws IOException;

    /**
     * 下载文件
     * @param request 请求参数
     * @return File 下载的文件
     * @throws IOException IOException
     */
    File getAsFile(Request request) throws IOException;

    /**
     * 文件上传
     * @param request 请求参数
     * @return Response
     * @throws IOException IOException
     */
    Response upload(Request request) throws IOException;

    /**
     * 对请求参数拦截处理 , 比如统一添加header , 参数加密 , 默认不处理
     * @param request Request
     * @return Request
     */
    default Request beforeTemplate(Request request){
        return Objects.requireNonNull(request);
    }

    /**
     * 对返回结果拦截处理 , 比如统一解密 , 默认不处理
     * @param request Request
     * @param response Response
     * @return Response
     * @throws IOException IOException
     */
    default Response afterTemplate(Request request, Response response) throws IOException{
        return response;
    }
}
```

```
public interface HttpClient {
    /**
     * 设置全局默认配置,不调用就用系统设置的
     * @param config config
     * @return 链式调用
     */
    HttpClient setConfig(Config config);
     /**
     *HTTP GET请求
     * @param url URL，可以帶参数
     * @param params 参数列表，可以为 null, 此系列get方法的params按照URLEncoder(UTF-8)拼装,
     *               如果是其他的编码请使用{@link SmartHttpClient#get(Request)},然后Request中设置bodyCharset
     * @param headers HTTP header 可以为 null
     * @param connectTimeout 连接超时时间
     * @param readTimeout 读超时时间
     * @param resultCharset 返回编码
     * @return 返回的内容
     * @throws IOException 超时异常 {@link java.net.SocketTimeoutException connect timed out/read time out}
     */
    String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException;

    default String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException{
        return get(url, params, headers, connectTimeout, readTimeout , null);
    }
    default String get(String url, Map<String, String> params, Map<String, String> headers, String resultCharset) throws IOException{
        return get(url,params,headers,null,null,resultCharset);
    }
    default String get(String url, Map<String, String> params, Map<String, String> headers) throws IOException{
        return get(url,params,headers,null,null);
    }
    default String get(String url, Map<String, String> params, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException{
        return get(url,params,null,connectTimeout,readTimeout,resultCharset);
    }
    default String get(String url, Map<String, String> params, Integer connectTimeout, Integer readTimeout) throws IOException{
        return get(url,params,null,connectTimeout,readTimeout);
    }
    default String get(String url, Map<String, String> params, String resultCharset) throws IOException{
        return get(url,params,null,null,null,resultCharset);
    }
    default String get(String url, Map<String, String> params) throws IOException{
        return get(url,params,null, null,(Integer) null);
    }
    default String get(String url, String resultCharset) throws IOException{
        return get(url,null,null,null,null, resultCharset);
    }
    default String get(String url) throws IOException{
        return get(url,null,null,null,(Integer)null);
    }


    /**
     * HTTP POST
     * @param url URL
     * @param body 请求体
     * @param contentType 请求体类型
     * @param headers 头
     * @param connectTimeout 连接超时时间
     * @param readTimeout 读超时时间
     * @param bodyCharset 请求体编码
     * @param resultCharset 返回编码
     * @return 请求返回
     * @throws IOException 超时异常 {@link java.net.SocketTimeoutException connect timed out/read time out}
     */
    String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException;

    default String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException{
        return post(url,body,contentType,headers,connectTimeout,readTimeout, null,null);
    }
    default String post(String url, String body, String contentType, Map<String, String> headers, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,contentType,headers,null,null,bodyCharset,resultCharset);
    }
    default String post(String url, String body, String contentType, Map<String, String> headers) throws IOException{
        return post(url,body,contentType,headers,null,(Integer) null);
    }
    default String post(String url, String body, String contentType, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,contentType,null,connectTimeout,readTimeout,bodyCharset,resultCharset);
    }
    default String post(String url, String body, String contentType, Integer connectTimeout, Integer readTimeout) throws IOException{
        return post(url,body,contentType,null,connectTimeout,readTimeout);
    }
    /**
     * @see HttpClient#post(String, String, String, Map, Integer, Integer, String, String)
     */
    default String post(String url, String body, String contentType, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,contentType,null,null,null,bodyCharset,resultCharset);
    }
    default String post(String url, String body, String contentType) throws IOException{
        return post(url,body,contentType,null,null,(Integer) null);
    }
    default String postJson(String url, String body, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,JSON_WITH_DEFAULT_CHARSET,null,null,null,bodyCharset,resultCharset);
    }
    default String postJson(String url, String body) throws IOException{
        return post(url,body,JSON_WITH_DEFAULT_CHARSET,null,null,(Integer) null);
    }

    /**参数用 =和& 连接*/
    default String post(String url, Map<String, String> params, Map<String, String> headers, String bodyCharset, String resultCharset) throws IOException{
        return post(url, ParamUtil.contactMap(params , bodyCharset),FORM_URLENCODED_WITH_DEFAULT_CHARSET,headers,bodyCharset,resultCharset);
    }
    default String post(String url, Map<String, String> params, Map<String, String> headers) throws IOException{
        return post(url, ParamUtil.contactMap(params , DEFAULT_CHARSET),FORM_URLENCODED_WITH_DEFAULT_CHARSET,headers);
    }
    default String post(String url, Map<String, String> params, String bodyCharset, String resultCharset) throws IOException{
        return post(url, ParamUtil.contactMap(params , bodyCharset),FORM_URLENCODED_WITH_DEFAULT_CHARSET,null,bodyCharset,resultCharset);
    }
    default String post(String url, Map<String, String> params) throws IOException{
        return post(url, ParamUtil.contactMap(params , DEFAULT_CHARSET),FORM_URLENCODED_WITH_DEFAULT_CHARSET,null);
    }

    /**
     * 文件下载相关，下载为字节数组
     */
    byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException;
    default byte[] getAsBytes(String url, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getAsBytes(url , null , connectTimeout , readTimeout);
    }
    default byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers) throws IOException{
        return getAsBytes(url , headers , null, null);
    }
    default byte[] getAsBytes(String url) throws IOException{
        return getAsBytes(url , null , null, null);
    }

    /**
     * 文件下载相关，下载为文件
     */
    File getAsFile(String url, ArrayListMultimap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException;
    default File getAsFile(String url, File file, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getAsFile(url , null  , file , connectTimeout , readTimeout);
    }
    default File getAsFile(String url, ArrayListMultimap<String, String> headers, File file) throws IOException{
        return getAsFile(url , headers  , file , null, null);
    }
    default File getAsFile(String url, File file) throws IOException{
        return getAsFile(url , null  , file , null, null);
    }

    /**
     * 上传文件
     * @param url URL
     * @param files 多个文件信息
     */
    String upload(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException;

    default String upload(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, FormFile... files) throws IOException{
        return upload(url, headers ,connectTimeout , readTimeout , null , files);
    }
    default String upload(String url, ArrayListMultimap<String, String> headers, FormFile... files) throws IOException{
        return upload(url, headers ,null, null, null , files);
    }
    default String upload(String url, Integer connectTimeout, Integer readTimeout, FormFile... files) throws IOException{
        return upload(url, null ,connectTimeout , readTimeout , null , files);
    }
    default String upload(String url, FormFile... files) throws IOException{
        return upload(url, null , null, null, null , files);
    }

    /**
     * 上传文件和key-value数据
     * @param url URL
     * @param files 多个文件信息
     */
    String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException;
    default String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, FormFile... files) throws IOException{
        return upload(url, params ,headers ,connectTimeout , readTimeout , null , files);
    }
    default String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, FormFile... files) throws IOException{
        return upload(url, params ,headers ,DEFAULT_CONNECT_TIMEOUT, null, null , files);
    }
    default String upload(String url, Integer connectTimeout, Integer readTimeout, ArrayListMultimap<String, String> params, FormFile... files) throws IOException{
        return upload(url, params ,null ,connectTimeout , readTimeout , null , files);
    }
    default String upload(String url, Map<String, String> params, FormFile... files) throws IOException{
        ArrayListMultimap<String , String> multimap = ArrayListMultimap.fromMap(params);
        return upload(url, multimap ,null , null, null, null , files);
    }
}
```

setConfig可以设置SmartHttpClient实例的全局默认设置。目前定义了一下一些参数。

```

/**
 * BaseUrl,如果设置了就在正常传送的URL之前添加上
 */
private String baseUrl;
/**
 * 连接超时时间
 */
private Integer defaultConnectionTimeout = HttpConstants.DEFAULT_CONNECT_TIMEOUT;
/**
 * 读数据超时时间
 */
private Integer defaultReadTimeout = HttpConstants.DEFAULT_READ_TIMEOUT;
/**
 * 请求体编码
 */
private String defaultBodyCharset = HttpConstants.DEFAULT_CHARSET;
/**
 * 返回体编码
 */
private String defaultResultCharset = HttpConstants.DEFAULT_CHARSET;

....

定义了这些可配置项，可以通过-D或者System.setProperty()全局设置，可以对某个实现的对象例如 `NativeSmartHttpClient` 全局设置，也可以针对某一个请求单独设置，优先级逐渐升高

```





https://gitee.com/xxssyyyyssxx/network/blob/master/src/test/java/top/jfunc/common/http/HttpBasicTest.java

https://gitee.com/xxssyyyyssxx/network/blob/master/src/test/java/top/jfunc/common/http/HttpSmartTest.java

https://gitee.com/xxssyyyyssxx/network/blob/master/src/test/java/top/jfunc/common/http/DelegateTest.java
