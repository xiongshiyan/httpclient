# httpclient: Define Abstract Interface of Httpclient and Provide Multiple Implementations

### introduction

本项目针对http协议定义了所有http实现之上的接口来描述一个http请求，使用者只需要面对同一套语义化的接口，
如果需要切换http的实现，可以做到完全无代码修改。支持GET、POST、文件上传下载等，目前适配了以下项目：
`OKHttp3、ApacheHttpComponents、HttpURLConnection、Jodd-Http`。

这一套接口基本覆盖了一个Http请求的所有参数，接口的使用也是简单的、统一的、一致的、连缀的。对URL、Header、Body、Form、文件上传提供最大的支持。

项目的两个亮点：
1. **http语义化的接口设计，简单的、统一的、一致的、连缀的接口使用体验**
2. **可以做到无代码修改切换Http请求的实现，而无需散弹式修改**。

### features

- [x] `SmartHttpClient`接口体系：基于`Request(HttpRequest)-Response`，可以通过设置一个特定过程的组件替换默认实现
- [x] `SmartHttpClient`接口继承于`HttpRequestHttpClient`和`SimpleHttpClient`，前者提供基于`HttpRequest`的请求，后者提供简单点的参数请求方式
- [x] `Request`支持链式调用、支持基于策略接口的Java对象转换为String、支持路径参数
- [x] `Response`支持基于策略接口的String转换为Java对象
- [x] 支持文件上传、下载
- [x] 支持https
- [x] 支持无代码修改的`OkHttp3、ApacheHttpClient、HttpURLConnection、JoddHttp`的切换
- [x] HttpUtil支持根据jar包的存在性加载实现
- [x] 可以对某个实现的对象例如 `NativeSmartHttpClient` 全局设置，也可以针对某一个请求Request单独设置，优先级逐渐升高
- [x] 通过`Config`全局配置默认参数
- [x] 支持全局header设置、全局Query参数
- [x] 支持请求之前之后加入特定的处理,复写`SmartHttpClient`的`beforeTemplate`和`afterTemplate`方法
- [x] `Proxy`代理支持
- [x] `HttpUtil`提供的静态方法完全代理`SmartHttpClient`接口，实现一句话完成Http请求
- [x] 从1.1.1版本开始Request分裂为表达每种请求的不同Request
- [x] 从1.1.1版本开始支持基于CookieHandler的Cookie支持,在全局设置中设置CookieHandler即可
- [x] 从1.1.2版本开始类似Retrofit、MyBatis-Mapper的接口使用方式
- [x] 从1.1.2版本开始支持全局拦截器拦截
- [x] 从1.1.5版本开始支持每个HttpRequest中设置属性便于后续使用
- [x] 从1.1.8版本开始支持所有的HTTP METHOD
- [ ] 文件上传支持断点续传

### how to import it?

##### 源码使用
下载本项目，gradle clean build得到的jar包引入工程即可。本项目依赖于[utils](https://gitee.com/xxssyyyyssxx/utils)


##### 项目管理工具导入 

项目已经发布至 `jcenter` 和 `maven` 中央仓库 最新版本version: **1.2.7**

Gradle:

```dsl
compile 'top.jfunc.network:httpclient:${version}'
```

Maven:

```xml
<!-- https://mvnrepository.com/artifact/top.jfunc.network/httpclient -->
<dependency>
    <groupId>top.jfunc.network</groupId>
    <artifactId>httpclient</artifactId>
    <version>${version}</version>
</dependency>
```

如果想去掉不必要的jar包引入可以排除特定的 

```
compile ('top.jfunc.network:httpclient:${version}'){
        ? exclude group:'org.apache.httpcomponents'
        ? exclude group:'com.squareup.okhttp3'
        ? exclude group:'org.jodd'
    }
```
```
<dependency>
    <groupId>top.jfunc.network</groupId>
    <artifactId>httpclient</artifactId>
    <version>${version}</version>
    <exclusions>  
        <exclusion>  ?
            <groupId>com.squareup.okhttp3</groupId>  
            <artifactId>okhttp</artifactId>  
        </exclusion>  
        <exclusion>  ?
            <groupId>org.apache.httpcomponents</groupId>  
            <artifactId>httpclient</artifactId>  
        </exclusion>  
        <exclusion>  ?
            <groupId>org.apache.httpcomponents</groupId>  
            <artifactId>httpmime</artifactId>  
        </exclusion> 
        <exclusion>  ?
            <groupId>org.jodd</groupId>  
            <artifactId>jodd-http</artifactId>  
        </exclusion>   
    </exclusions>  
</dependency>
```

### how to use it?

**面向SmartHttpClient**

1. 可以使用HttpUtil的delegate获取一个实现，或者自己实例化一个；
2. 在SpringBoot项目中，用Bean注入；
3. HttpUtil实现了对接口SmartHttpClient的完全静态化代理，一句话实现Http请求。

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
        smartHttpClient.setConfig(Config.defaultConfig()...);//设置baseUrl...
        retrun smartHttpClient;
    }
}
```

当拿到实例之后，就可以使用接口定义的所有的方法用于http请求。
 **HttpClient接口定义了基本的http请求方法，SmartHttpClient继承于HttpClient，新增了基于Request的方法** 见架构图。

setConfig可以设置SmartHttpClient实例的全局默认设置。目前定义了一下一些参数。

```

/**BaseUrl,如果设置了就在正常传送的URL之前添加上*/
private String baseUrl                                  = null;
/**连接超时时间*/
private int defaultConnectionTimeout                    = DEFAULT_TIMEOUT;
/**读数据超时时间*/
private int defaultReadTimeout                          = DEFAULT_TIMEOUT;
/**请求体编码*/
private String defaultBodyCharset                       = CharsetUtil.UTF_8;
/**返回体编码*/
private String defaultResultCharset                     = CharsetUtil.UTF_8;
/**返回结果中是否保留headers,默认不保留*/
private boolean retainResponseHeaders                   = !RETAIN_RESPONSE_HEADERS;
/**返回结果中是否忽略body,默认不忽略*/
private boolean ignoreResponseBody                      = !IGNORE_RESPONSE_BODY;
/**是否支持重定向,默认不支持*/
private boolean followRedirects                         = !FOLLOW_REDIRECTS;
/**代理设置,如果有就设置*/
private ProxyInfo proxyInfo                             = null;
/**SSL相关设置处理器*/
private SSLHolder sslHolder                             = new DefaultSSLHolder2();
/**默认的请求头,每个请求都会加上*/
private HeaderHolder headerHolder                       = new DefaultHeaderHolder();
/**默认的请求Query参数,每个请求都会加上*/
private ParamHolder queryParamHolder                    = new DefaultParamHolder();
/**CookieJar,处理Cookie*/
private CookieJar cookieJar                             = null;

....

定义了这些可配置项，可以对某个实现的对象例如 `NativeSmartHttpClient` 全局设置，也可以针对某一个请求单独设置，优先级逐渐升高

```





https://gitee.com/xxssyyyyssxx/httpclient/blob/master/src/test/java/top/jfunc/common/http/HttpBasicTest.java

https://gitee.com/xxssyyyyssxx/httpclient/blob/master/src/test/java/top/jfunc/common/http/HttpSmartTest.java

https://gitee.com/xxssyyyyssxx/httpclient/blob/master/src/test/java/top/jfunc/common/http/DelegateTest.java

下面演示几种用法：
1. Request代表所有请求的变量，支持链式编程
2. SmartHttpClient接口的实现类完成真正的请求
3. Response代表返回信息，可以根据需要转换为字节数组、字符串。。。

GET:
```java
Response response = http.get(Request.of(url).setIgnoreResponseBody(false).setIncludeHeaders(true).addHeader("saleType" , "2").setResultCharset("UTF-8"));
System.out.println(response);
System.out.println("headers:" + response.getHeaders());

String s = http.get(url);
System.out.println(s);

Request request = Request.of(url).addParam("xx" , "xx").addParam("yy" , "yy").addHeader("saleType" , "2").setResultCharset("UTF-8");
byte[] bytes = http.getAsBytes(request);
System.out.println(bytes.length);
System.out.println(new String(bytes));

request = Request.of(url).setFile(new File("C:\\Users\\xiongshiyan\\Desktop\\yyyy.txt"));
File asFile = http.getAsFile(request);
System.out.println(asFile.getAbsolutePath());
```

POST:
```java
Request request = Request.of(url).setIncludeHeaders(true).addHeader("ss" , "ss").addHeader("ss" , "dd").setBody("{\"name\":\"熊诗言\"}").setContentType(JSON_WITH_DEFAULT_CHARSET).setConnectionTimeout(10000).setReadTimeout(10000).setResultCharset("UTF-8");
Response post = http.post(request);
System.out.println(post.getBody());
System.out.println(post.getHeaders());

String s = http.postJson(url, "{\"name\":\"熊诗言\"}");
System.out.println(s);

request = Request.of(url).addParam("xx" , "xx").addParam("yy" , "yy").setContentType(FORM_URLENCODED);
Response response = http.post(request);
System.out.println(response.getBody());
```

UPLOAD:
```java
FormFile formFile = new FormFile(new File("E:\\838586397836550106.jpg") , "filedata",null);
Request request = Request.of(url).addHeader("empCode" , "ahg0023")
        .addHeader("phone" , "15208384257").addFormFile(formFile).setIncludeHeaders(true);
Response response = httpClient.upload(request);
System.out.println(response.getBody());
System.out.println(response.getHeaders());
```

多文件及带参数的上传:
```java
FormFile formFile = new FormFile(new File("E:\\838586397836550106.jpg") , "filedata",null);
FormFile formFile2 = new FormFile(new File("E:\\BugReport.png") , "filedata2",null);
Request request = Request.of(url).addHeader("empCode" , "ahg0023")
        .addHeader("phone" , "15208384257").addFormFile(formFile2).addFormFile(formFile).setIncludeHeaders(true);

request.addParam("k1", "v1").addParam("k2" , "v2");
Response response = httpClient.upload(request);
System.out.println(response.getBody());
System.out.println(response.getHeaders());
```

对SmartHttpClient设置全局默认参数
```java
http.setConfig(Config.defaultConfig()
            .setBaseUrl("https://fanyi.baidu.com/")
            .addDefaultHeader("xx" , "xx")
            .setDefaultBodyCharset("UTF-8")
            .setDefaultResultCharset("UTF-8")
            .setDefaultConnectionTimeout(15000)
            .setDefaultReadTimeout(15000)

    //.....
   );
```

类似MyBatis接口使用方式

1.配置HttpService接口扫描 
2.定义如下一样的接口

```java
    @Configuration
    public class SomeConfiguration{
        @Bean
        public SmartHttpClient smartHttpClient(){
            Config config = Config.defaultConfig().setBaseUrl("xxxxx);
            NativeSmartHttpClient smartHttpClient = new NativeSmartHttpClient();
            smartHttpClient.setConfig(config);
            return smartHttpClient;
        }
        //以下配置可以扫描 top.jfunc.network.controller.client 包下的标注 @HttpService 注解的接口
        @Bean
        public HttpServiceCreator httpServiceCreator(SmartHttpClient smartHttpClient){
            return new HttpServiceCreator().setSmartHttpClient(smartHttpClient);
        }
        @Bean
        public HttpServiceScanConfigure httpServiceScanConfigure(){
            HttpServiceScanConfigure httpServiceScanConfigure = new HttpServiceScanConfigure(httpServiceCreator(smartHttpClient()));
            httpServiceScanConfigure.setAnnotationClassScan(HttpService.class);
            httpServiceScanConfigure.setScanPackages("top.jfunc.network.controller.client");
            return httpServiceScanConfigure;
        }
    }
    



    @HttpService
    public interface InterfaceForTestHttpService {
    
        @GET
        Response request(HttpRequest httpRequest);
    
        @GET("/get/{q}")
        Response list(@Path("q") String q, @Query("xx") int xx);
        @GET("/get/query")
        Response queryMap(@QueryMap Map<String, String> map);
        @GET
        Response url(@Url String url);
    
        @GET("get/query")
        Response header(@Header("naked") String naked);
    
        @Headers({"xx:xiongshiyan","yy:xsy"})
        @GET("get/query")
        Response headers(@Header("naked") String naked);
    
        @GET("get/query")
        Response headerMap(@HeaderMap Map<String, String> map);
    
    
    
        @GET("/get/query")
        Response download();
    
        @POST("/post/{id}")
        Response post(@Path("id") String id, @Body String xx);
    
        @Multipart
        @POST("/upload/only")
        Response upload(@Part FormFile... formFiles);
        @Multipart
        @POST("/upload/withParam")
        Response uploadWithParam(@Part("name") String name, @Part("age") int age, @Part FormFile... formFiles);
    
        @FormUrlEncoded
        @POST("/post/form")
        Response form(@Field("name") String name, @Field("age") int age);
        @FormUrlEncoded
        @POST("/post/form")
        Response formMap(@FieldMap Map<String, String> params);
    }


```





建议的最佳实践为：
1. 设置body的同时应该设置Content-Type
2. 针对同一组请求可以使用一个SmartHttpClient，可以对他进行一些参数设置
3. 使用拦截器而非复写父类方法达到拦截目的

更多用法等待你探索，本人才疏学浅，难免有考虑不周到的地方，请不吝赐教。

提供了服务端的测试工程，可以clone下来运行，再跑本项目的单元测试。https://gitee.com/xxssyyyyssxx/http-server-test


http模块的架构设计和使用方式见 CSDN博客

[一个http请求工具类的接口化（接口设计）](https://blog.csdn.net/xxssyyyyssxx/article/details/80715202)

[一个http请求工具类的接口化（多种实现）](https://blog.csdn.net/xxssyyyyssxx/article/details/80715837)

项目采用双接口并行设计模式，一种是面向接口实现者的 **HttpTemplate-SmartHttpTemplate**  _功能接口_ ，主要的功能是模拟Http的参数、header等；一种是面向终端用户调用者的 **HttpClient-SmartHttpClient**  _用户接口_ 。具体实现类通过实现两组接口，后者接口实现最终调用前者的接口实现。这样做的好处是互不影响，实现者可以无限优化 **HttpTemplate、SmartHttpTemplate** 的实现类，而对使用者几无影响。

HttpTemplate、HttpClient接口基于方法来模拟Http的参数、header等；SmartHttpTemplate继承于HttpTemplate，SmartHttpClient接口继承于HttpClient，且基于Request来模拟Http的参数、header，这样更容易优化、更容易使用。

[接口体系设计及类图说明](https://gitee.com/xxssyyyyssxx/httpclient/blob/master/CLASS.md)