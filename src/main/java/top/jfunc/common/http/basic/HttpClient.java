package top.jfunc.common.http.basic;

import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.smart.SmartHttpClient;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static top.jfunc.common.http.HttpConstants.*;

/**
 * GET POST接口
 * @author 熊诗言2017/11/24
 * @see SmartHttpClient
 * 针对Http超时和各种错误码分别处理
 * 使用时，可以直接new实现类，也可以通过{@link top.jfunc.common.http.HttpUtil }获取，这样就不会与实现类绑定
 */
public interface HttpClient {
    /**
     * 设置全局默认配置,不调用就用系统设置的
     * @param config config
     */
    void setConfig(Config config);

    /**
     * 获取全局配置
     * @return 全局配置的config
     */
    Config getConfig();

     /**
     *HTTP GET请求
     * @param url URL，可以帶参数
     * @param params 参数列表，可以为 null, 此系列get方法的params按照URLEncoder(UTF-8)拼装,
     *               如果是其他的编码请使用{@link SmartHttpClient#get(top.jfunc.common.http.request.HttpRequest)},然后Request中设置bodyCharset
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
    byte[] getAsBytes(String url, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException;
    default byte[] getAsBytes(String url, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getAsBytes(url , null , connectTimeout , readTimeout);
    }
    default byte[] getAsBytes(String url, MultiValueMap<String, String> headers) throws IOException{
        return getAsBytes(url , headers , null, null);
    }
    default byte[] getAsBytes(String url) throws IOException{
        return getAsBytes(url , null , null, null);
    }

    /**
     * 文件下载相关，下载为文件
     */
    File getAsFile(String url, MultiValueMap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException;
    default File getAsFile(String url, File file, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getAsFile(url , null  , file , connectTimeout , readTimeout);
    }
    default File getAsFile(String url, MultiValueMap<String, String> headers, File file) throws IOException{
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
    String upload(String url, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException;

    default String upload(String url, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, FormFile... files) throws IOException{
        return upload(url, headers ,connectTimeout , readTimeout , null , files);
    }
    default String upload(String url, MultiValueMap<String, String> headers, FormFile... files) throws IOException{
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
    String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException;
    default String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, FormFile... files) throws IOException{
        return upload(url, params ,headers ,connectTimeout , readTimeout , null , files);
    }
    default String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, FormFile... files) throws IOException{
        return upload(url, params ,headers ,DEFAULT_CONNECT_TIMEOUT, null, null , files);
    }
    default String upload(String url, Integer connectTimeout, Integer readTimeout, MultiValueMap<String, String> params, FormFile... files) throws IOException{
        return upload(url, params ,null ,connectTimeout , readTimeout , null , files);
    }
    default String upload(String url, Map<String, String> params, FormFile... files) throws IOException{
        MultiValueMap<String , String> multimap = ArrayListMultiValueMap.fromMap(params);
        return upload(url, multimap ,null , null, null, null , files);
    }
}
