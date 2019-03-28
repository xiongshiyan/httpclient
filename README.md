# network

#### 项目介绍
网络处理模块

## 1.http客户端请求工具类，有多种实现：OKHttp3、ApacheHttpClient、HttpURLConnection，可以随意切换http实现
## 2.ftp接口及实现

http模块的架构设计和使用方式见CSDN博客
[一个http请求工具类的接口化（接口设计）](https://blog.csdn.net/xxssyyyyssxx/article/details/80715202)
[一个http请求工具类的接口化（多种实现）](https://blog.csdn.net/xxssyyyyssxx/article/details/80715837)

### 使用方式
下载本项目，gradle clean build得到的jar包引入工程即可。本项目依赖于[utils](https://gitee.com/xxssyyyyssxx/utils)

version:1.8.1

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
#### 5.如果想只使用FTP 
compile ('top.jfunc.common:network:${version}'){
        exclude group:'org.apache.httpcomponents'
        exclude group:'com.squareup.okhttp3'
    }



具体的使用方式：面向SmartHttpClient，可以使用HttpUtil的delegate获取一个实现，或者自己实例化一个。在SpringBoot项目中，用Bean注入：

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

定义了这些可配置项，可以通过-D全局设置，可以对某个实现的对象例如 `NativeSmartHttpClient` 全局设置，也可以针对某一个请求单独设置，优先级逐渐升高

```



https://gitee.com/xxssyyyyssxx/network/blob/master/src/test/java/top/jfunc/common/http/HttpBasicTest.java

https://gitee.com/xxssyyyyssxx/network/blob/master/src/test/java/top/jfunc/common/http/HttpSmartTest.java

https://gitee.com/xxssyyyyssxx/network/blob/master/src/test/java/top/jfunc/common/http/DelegateTest.java
