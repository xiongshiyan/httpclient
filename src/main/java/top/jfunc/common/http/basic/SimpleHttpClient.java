package top.jfunc.common.http.basic;

import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ConfigAccessor;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.SmartHttpClient;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static top.jfunc.common.http.HttpConstants.*;

/**
 * GET POST接口
 * 此接口可以实现一些简单的Http请求
 * 从接口定义看出，改变一个参数就需要重新定义个方法，实在太啰嗦，并且不好维护，
 * 所以墙裂建议使用其子类{@link SmartHttpClient}，
 * 使用{@link HttpRequest} 及子类
 * 或者{@link top.jfunc.common.http.smart.Request}来表达请求参数
 * 使用{@link SmartHttpClient}还可以实现一些高级功能，比如拦截。
 * @author 熊诗言
 * @since 2017/11/24
 */
public interface SimpleHttpClient extends ConfigAccessor {
     /**
     * HTTP GET请求
     * @param url URL，可以帶参数
     * @param params 参数列表，可以为 null, 此系列get方法的params按照URLEncoder(UTF-8)拼装,
     *               如果是其他的编码请使用{@link SmartHttpClient#get(HttpRequest)},然后Request中设置bodyCharset
     * @param headers HTTP header 可以为 null
     * @param connectTimeout 连接超时时间
     * @param readTimeout 读超时时间
     * @param resultCharset 返回编码
     * @return 返回的内容
     * @throws IOException 超时异常 {@link java.net.SocketTimeoutException connect timed out/read time out}
     */
    String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException;

    /**
     * HTTP GET请求
     * @see SimpleHttpClient#get(String, Map, Map, Integer, Integer, String)
     * @param url url
     * @param params params
     * @param headers headers
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @return String
     * @throws IOException IOException
     */
    default String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException{
        return get(url, params, headers, connectTimeout, readTimeout , null);
    }

    /**
     * HTTP GET请求
     * @see SimpleHttpClient#get(String, Map, Map, Integer, Integer, String)
     * @param url url
     * @param params params
     * @param headers headers
     * @param resultCharset resultCharset
     * @return String
     * @throws IOException IOException
     */
    default String get(String url, Map<String, String> params, Map<String, String> headers, String resultCharset) throws IOException{
        return get(url,params,headers,null,null,resultCharset);
    }

    /**
     * HTTP GET请求
     * @see SimpleHttpClient#get(String, Map, Map, Integer, Integer, String)
     * @param url url
     * @param params params
     * @param headers headers
     * @return String
     * @throws IOException IOException
     */
    default String get(String url, Map<String, String> params, Map<String, String> headers) throws IOException{
        return get(url,params,headers,null,null);
    }

    /**
     * HTTP GET请求
     * @see SimpleHttpClient#get(String, Map, Map, Integer, Integer, String)
     * @param url url
     * @param params params
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @param resultCharset resultCharset
     * @return String
     * @throws IOException IOException
     */
    default String get(String url, Map<String, String> params, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException{
        return get(url,params,null,connectTimeout,readTimeout,resultCharset);
    }

    /**
     * HTTP GET请求
     * @see SimpleHttpClient#get(String, Map, Map, Integer, Integer, String)
     * @param url url
     * @param params params
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @return String
     * @throws IOException IOException
     */
    default String get(String url, Map<String, String> params, Integer connectTimeout, Integer readTimeout) throws IOException{
        return get(url,params,null,connectTimeout,readTimeout);
    }

    /**
     * HTTP GET请求
     * @see SimpleHttpClient#get(String, Map, Map, Integer, Integer, String)
     * @param url url
     * @param params params
     * @param resultCharset resultCharset
     * @return String
     * @throws IOException IOException
     */
    default String get(String url, Map<String, String> params, String resultCharset) throws IOException{
        return get(url,params,null,null,null,resultCharset);
    }

    /**
     * HTTP GET请求
     * @see SimpleHttpClient#get(String, Map, Map, Integer, Integer, String)
     * @param url url
     * @param params params
     * @return String
     * @throws IOException IOException
     */
    default String get(String url, Map<String, String> params) throws IOException{
        return get(url,params,null, null,(Integer) null);
    }

    /**
     * HTTP GET请求
     * @see SimpleHttpClient#get(String, Map, Map, Integer, Integer, String)
     * @param url url
     * @param resultCharset resultCharset
     * @return String
     * @throws IOException IOException
     */
    default String get(String url, String resultCharset) throws IOException{
        return get(url,null,null,null,null, resultCharset);
    }

    /**
     * HTTP GET请求
     * @see SimpleHttpClient#get(String, Map, Map, Integer, Integer, String)
     * @param url url
     * @return String
     * @throws IOException IOException
     */
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

    /**
     * HTTP POST
     * @see SimpleHttpClient#post(String, String, String, Map, Integer, Integer, String, String)
     * @param url url
     * @param body body
     * @param contentType contentType
     * @param headers headers
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @return String
     * @throws IOException IOException
     */
    default String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException{
        return post(url,body,contentType,headers,connectTimeout,readTimeout, null,null);
    }

    /**
     * HTTP POST
     * @see SimpleHttpClient#post(String, String, String, Map, Integer, Integer, String, String)
     * @param url url
     * @param body body
     * @param contentType contentType
     * @param headers headers
     * @param bodyCharset bodyCharset
     * @param resultCharset resultCharset
     * @return String
     * @throws IOException IOException
     */
    default String post(String url, String body, String contentType, Map<String, String> headers, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,contentType,headers,null,null,bodyCharset,resultCharset);
    }

    /**
     * HTTP POST
     * @see SimpleHttpClient#post(String, String, String, Map, Integer, Integer, String, String)
     * @param url url
     * @param body body
     * @param contentType contentType
     * @param headers headers
     * @return String
     * @throws IOException IOException
     */
    default String post(String url, String body, String contentType, Map<String, String> headers) throws IOException{
        return post(url,body,contentType,headers,null,(Integer) null);
    }

    /**
     * HTTP POST
     * @see SimpleHttpClient#post(String, String, String, Map, Integer, Integer, String, String)
     * @param url url
     * @param body body
     * @param contentType contentType
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @param bodyCharset bodyCharset
     * @param resultCharset resultCharset
     * @return String
     * @throws IOException IOException
     */
    default String post(String url, String body, String contentType, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,contentType,null,connectTimeout,readTimeout,bodyCharset,resultCharset);
    }

    /**
     * HTTP POST
     * @see SimpleHttpClient#post(String, String, String, Map, Integer, Integer, String, String)
     * @param url url
     * @param body body
     * @param contentType contentType
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @return String
     * @throws IOException IOException
     */
    default String post(String url, String body, String contentType, Integer connectTimeout, Integer readTimeout) throws IOException{
        return post(url,body,contentType,null,connectTimeout,readTimeout);
    }

    /**
     * HTTP POST
     * @see SimpleHttpClient#post(String, String, String, Map, Integer, Integer, String, String)
     * @param url url
     * @param body body
     * @param contentType contentType
     * @param bodyCharset bodyCharset
     * @param resultCharset resultCharset
     * @return String
     * @throws IOException IOException
     */
    default String post(String url, String body, String contentType, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,contentType,null,null,null,bodyCharset,resultCharset);
    }

    /**
     * HTTP POST
     * @see SimpleHttpClient#post(String, String, String, Map, Integer, Integer, String, String)
     * @param url url
     * @param body body
     * @param contentType contentType
     * @return String
     * @throws IOException IOException
     */
    default String post(String url, String body, String contentType) throws IOException{
        return post(url,body,contentType,null,null,(Integer) null);
    }

    /**
     * HTTP POST
     * @see SimpleHttpClient#post(String, String, String, Map, Integer, Integer, String, String)
     * @param url url
     * @param body body
     * @param bodyCharset bodyCharset
     * @param resultCharset resultCharset
     * @return String
     * @throws IOException IOException
     */
    default String postJson(String url, String body, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,JSON_WITH_DEFAULT_CHARSET,null,null,null,bodyCharset,resultCharset);
    }

    /**
     * HTTP POST
     * @see SimpleHttpClient#post(String, String, String, Map, Integer, Integer, String, String)
     * @param url url
     * @param body body
     * @return String
     * @throws IOException IOException
     */
    default String postJson(String url, String body) throws IOException{
        return post(url,body,JSON_WITH_DEFAULT_CHARSET,null,null,(Integer) null);
    }

    /**
     * HTTP POST form
     * @see SimpleHttpClient#post(String, String, String, Map, Integer, Integer, String, String)
     * @see ParamUtil#contactMap(Map)
     * @param url url
     * @param params params 参数用 =和& 连接
     * @param headers headers
     * @param bodyCharset bodyCharset
     * @param resultCharset resultCharset
     * @return String
     * @throws IOException IOException
     */
    default String post(String url, Map<String, String> params, Map<String, String> headers, String bodyCharset, String resultCharset) throws IOException{
        return post(url, ParamUtil.contactMap(params , bodyCharset),FORM_URLENCODED_WITH_DEFAULT_CHARSET,headers,bodyCharset,resultCharset);
    }

    /**
     * HTTP POST form
     * @see SimpleHttpClient#post(String, Map, Map, String, String)
     * @param url url
     * @param params params 参数用 =和& 连接
     * @param headers headers
     * @return String
     * @throws IOException IOException
     */
    default String post(String url, Map<String, String> params, Map<String, String> headers) throws IOException{
        return post(url, ParamUtil.contactMap(params , DEFAULT_CHARSET),FORM_URLENCODED_WITH_DEFAULT_CHARSET,headers);
    }

    /**
     * HTTP POST form
     * @see SimpleHttpClient#post(String, Map, Map, String, String)
     * @param url url
     * @param params params 参数用 =和& 连接
     * @param bodyCharset bodyCharset
     * @param resultCharset resultCharset
     * @return String
     * @throws IOException IOException
     */
    default String post(String url, Map<String, String> params, String bodyCharset, String resultCharset) throws IOException{
        return post(url, ParamUtil.contactMap(params , bodyCharset),FORM_URLENCODED_WITH_DEFAULT_CHARSET,null,bodyCharset,resultCharset);
    }

    /**
     * HTTP POST form
     * @see SimpleHttpClient#post(String, Map, Map, String, String)
     * @param url url
     * @param params params 参数用 =和& 连接
     * @return String
     * @throws IOException IOException
     */
    default String post(String url, Map<String, String> params) throws IOException{
        return post(url, ParamUtil.contactMap(params , DEFAULT_CHARSET),FORM_URLENCODED_WITH_DEFAULT_CHARSET,null);
    }

    /**
     * 下载为字节数组
     * @param url url
     * @param headers headers
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @return byte[]
     * @throws IOException IOException
     */
    byte[] getAsBytes(String url, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException;

    /**
     * 下载为字节数组
     * @see SimpleHttpClient#getAsBytes(String, MultiValueMap, Integer, Integer)
     * @param url url
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @return byte[]
     * @throws IOException IOException
     */
    default byte[] getAsBytes(String url, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getAsBytes(url , null , connectTimeout , readTimeout);
    }

    /**
     * 下载为字节数组
     * @see SimpleHttpClient#getAsBytes(String, MultiValueMap, Integer, Integer)
     * @param url url
     * @param headers headers
     * @return byte[]
     * @throws IOException IOException
     */
    default byte[] getAsBytes(String url, MultiValueMap<String, String> headers) throws IOException{
        return getAsBytes(url , headers , null, null);
    }

    /**
     * 下载为字节数组
     * @see SimpleHttpClient#getAsBytes(String, MultiValueMap, Integer, Integer)
     * @param url url
     * @return byte[]
     * @throws IOException IOException
     */
    default byte[] getAsBytes(String url) throws IOException{
        return getAsBytes(url , null , null, null);
    }

    /**
     * 下载为文件
     * @param url url
     * @param headers headers
     * @param file file
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @return File
     * @throws IOException IOException
     */
    File getAsFile(String url, MultiValueMap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException;

    /**
     * 下载为文件
     * @see SimpleHttpClient#getAsFile(String, File, Integer, Integer)
     * @param url url
     * @param file file
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @return File
     * @throws IOException IOException
     */
    default File getAsFile(String url, File file, Integer connectTimeout, Integer readTimeout) throws IOException{
        return getAsFile(url , null  , file , connectTimeout , readTimeout);
    }
    /**
     * 下载为文件
     * @see SimpleHttpClient#getAsFile(String, File, Integer, Integer)
     * @param url url
     * @param headers headers
     * @param file file
     * @return File
     * @throws IOException IOException
     */
    default File getAsFile(String url, MultiValueMap<String, String> headers, File file) throws IOException{
        return getAsFile(url , headers  , file , null, null);
    }
    /**
     * 下载为文件
     * @see SimpleHttpClient#getAsFile(String, File, Integer, Integer)
     * @param url url
     * @param file file
     * @return File
     * @throws IOException IOException
     */
    default File getAsFile(String url, File file) throws IOException{
        return getAsFile(url , null  , file , null, null);
    }

    /**
     * 上传文件
     * @param url url
     * @param headers headers
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @param resultCharset resultCharset
     * @param files files
     * @return String
     * @throws IOException IOException
     */
    String upload(String url, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException;

    /**
     * 上传文件
     * @see SimpleHttpClient#upload(String, MultiValueMap, Integer, Integer, String, FormFile...)
     * @param url url
     * @param headers headers
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @param files files
     * @return String
     * @throws IOException IOException
     */
    default String upload(String url, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, FormFile... files) throws IOException{
        return upload(url, headers ,connectTimeout , readTimeout , null , files);
    }

    /**
     * 上传文件
     * @see SimpleHttpClient#upload(String, MultiValueMap, Integer, Integer, String, FormFile...)
     * @param url url
     * @param headers headers
     * @param files files
     * @return String
     * @throws IOException IOException
     */
    default String upload(String url, MultiValueMap<String, String> headers, FormFile... files) throws IOException{
        return upload(url, headers ,null, null, null , files);
    }

    /**
     * 上传文件
     * @see SimpleHttpClient#upload(String, MultiValueMap, Integer, Integer, String, FormFile...)
     * @param url url
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @param files files
     * @return String
     * @throws IOException IOException
     */
    default String upload(String url, Integer connectTimeout, Integer readTimeout, FormFile... files) throws IOException{
        return upload(url, null ,connectTimeout , readTimeout , null , files);
    }

    /**
     * 上传文件
     * @see SimpleHttpClient#upload(String, MultiValueMap, Integer, Integer, String, FormFile...)
     * @param url url
     * @param files files
     * @return String
     * @throws IOException IOException
     */
    default String upload(String url, FormFile... files) throws IOException{
        return upload(url, null , null, null, null , files);
    }

    /**
     * 上传文件和key-value数据
     * @param url url
     * @param params params
     * @param headers headers
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @param resultCharset resultCharset
     * @param files files
     * @return String
     * @throws IOException IOException
     */
    String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException;

    /**
     * 上传文件和key-value数据
     * @see SimpleHttpClient#upload(String, MultiValueMap, MultiValueMap, Integer, Integer, String, FormFile...)
     * @param url url
     * @param params params
     * @param headers headers
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @param files files
     * @return String
     * @throws IOException IOException
     */
    default String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, FormFile... files) throws IOException{
        return upload(url, params ,headers ,connectTimeout , readTimeout , null , files);
    }

    /**
     * 上传文件和key-value数据
     * @see SimpleHttpClient#upload(String, MultiValueMap, MultiValueMap, Integer, Integer, String, FormFile...)
     * @param url url
     * @param params params
     * @param headers headers
     * @param files files
     * @return String
     * @throws IOException IOException
     */
    default String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, FormFile... files) throws IOException{
        return upload(url, params ,headers ,DEFAULT_CONNECT_TIMEOUT, null, null , files);
    }

    /**
     * 上传文件和key-value数据
     * @see SimpleHttpClient#upload(String, MultiValueMap, MultiValueMap, Integer, Integer, String, FormFile...)
     * @param url url
     * @param params params
     * @param connectTimeout connectTimeout
     * @param readTimeout readTimeout
     * @param files files
     * @return String
     * @throws IOException IOException
     */
    default String upload(String url, Integer connectTimeout, Integer readTimeout, MultiValueMap<String, String> params, FormFile... files) throws IOException{
        return upload(url, params ,null ,connectTimeout , readTimeout , null , files);
    }

    /**
     * 上传文件和key-value数据
     * @see SimpleHttpClient#upload(String, MultiValueMap, MultiValueMap, Integer, Integer, String, FormFile...)
     * @param url url
     * @param params params
     * @param files files
     * @return String
     * @throws IOException IOException
     */
    default String upload(String url, Map<String, String> params, FormFile... files) throws IOException{
        MultiValueMap<String , String> multimap = ArrayListMultiValueMap.fromMap(params);
        return upload(url, multimap ,null , null, null, null , files);
    }
}
