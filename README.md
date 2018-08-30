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

version:1.8.0

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