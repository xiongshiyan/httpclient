package cn.zytx.common.http.basic;

import cn.zytx.common.http.HttpException;
import cn.zytx.common.http.ParamUtil;
import cn.zytx.common.http.smart.Request;
import cn.zytx.common.http.smart.SmartHttpClient;
import cn.zytx.common.utils.ArrayListMultimap;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static cn.zytx.common.http.HttpConstants.*;

/**
 * GET POST接口
 * @author 熊诗言2017/11/24
 * @see SmartHttpClient
 * 针对Http超时和各种错误码分别处理
 * 使用时，可以直接new实现类，也可以通过{@link cn.zytx.common.utils.SpringFactoriesLoader }获取，这样就不会与实现类绑定
 */
public interface HttpClient {
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
     * @throws HttpException 非200请求异常(code就是http的statusCode)或者其他异常(code为-1)抛出
     */
    String get(String url, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout, String resultCharset) throws IOException;

    default String get(String url, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout) throws IOException{
        return get(url, params, headers, connectTimeout, readTimeout , DEFAULT_CHARSET);
    }
    default String get(String url, Map<String, String> params, Map<String, String> headers, String resultCharset) throws IOException{
        return get(url,params,headers,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT,resultCharset);
    }
    default String get(String url, Map<String, String> params, Map<String, String> headers) throws IOException{
        return get(url,params,headers,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT);
    }
    default String get(String url, Map<String, String> params, int connectTimeout, int readTimeout, String resultCharset) throws IOException{
        return get(url,params,null,connectTimeout,readTimeout,resultCharset);
    }
    default String get(String url, Map<String, String> params, int connectTimeout, int readTimeout) throws IOException{
        return get(url,params,null,connectTimeout,readTimeout);
    }
    default String get(String url, Map<String, String> params, String resultCharset) throws IOException{
        return get(url,params,null,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT,resultCharset);
    }
    default String get(String url, Map<String, String> params) throws IOException{
        return get(url,params,null,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT);
    }
    default String get(String url, String resultCharset) throws IOException{
        return get(url,null,null,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT, resultCharset);
    }
    default String get(String url) throws IOException{
        return get(url,null,null,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT);
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
     * @throws HttpException 非200请求异常(code就是http的statusCode)或者其他异常(code为-1)抛出
     */
    String post(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) throws IOException;

    default String post(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout) throws IOException{
        return post(url,body,contentType,headers,connectTimeout,readTimeout, DEFAULT_CHARSET,DEFAULT_CHARSET);
    }
    default String post(String url, String body, String contentType, Map<String, String> headers, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,contentType,headers,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT,bodyCharset,resultCharset);
    }
    default String post(String url, String body, String contentType, Map<String, String> headers) throws IOException{
        return post(url,body,contentType,headers,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT);
    }
    default String post(String url, String body, String contentType, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,contentType,null,connectTimeout,readTimeout,bodyCharset,resultCharset);
    }
    default String post(String url, String body, String contentType, int connectTimeout, int readTimeout) throws IOException{
        return post(url,body,contentType,null,connectTimeout,readTimeout);
    }
    /**
     * @see HttpClient#post(String, String, String, Map, int, int, String, String)
     */
    default String post(String url, String body, String contentType, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,contentType,null,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT,bodyCharset,resultCharset);
    }
    default String post(String url, String body, String contentType) throws IOException{
        return post(url,body,contentType,null,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT);
    }
    default String postJson(String url, String body, String bodyCharset, String resultCharset) throws IOException{
        return post(url,body,JSON_WITH_DEFAULT_CHARSET,null,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT,bodyCharset,resultCharset);
    }
    default String postJson(String url, String body) throws IOException{
        return post(url,body,JSON_WITH_DEFAULT_CHARSET,null,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT);
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
    byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout) throws IOException;
    default byte[] getAsBytes(String url, int connectTimeout, int readTimeout) throws IOException{
        return getAsBytes(url , null , connectTimeout , readTimeout);
    }
    default byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers) throws IOException{
        return getAsBytes(url , headers , DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }
    default byte[] getAsBytes(String url) throws IOException{
        return getAsBytes(url , null , DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * 文件下载相关，下载为文件
     */
    File getAsFile(String url, ArrayListMultimap<String, String> headers, File file, int connectTimeout, int readTimeout) throws IOException;
    default File getAsFile(String url, File file, int connectTimeout, int readTimeout) throws IOException{
        return getAsFile(url , null  , file , connectTimeout , readTimeout);
    }
    default File getAsFile(String url, ArrayListMultimap<String, String> headers, File file) throws IOException{
        return getAsFile(url , headers  , file , DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }
    default File getAsFile(String url, File file) throws IOException{
        return getAsFile(url , null  , file , DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * 上传文件
     * @param url URL
     * @param files 多个文件信息
     */
    String upload(String url, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) throws IOException;

    default String upload(String url, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, FormFile... files) throws IOException{
        return upload(url, headers ,connectTimeout , readTimeout , DEFAULT_CHARSET , files);
    }
    default String upload(String url, ArrayListMultimap<String, String> headers, FormFile... files) throws IOException{
        return upload(url, headers ,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_CHARSET , files);
    }
    default String upload(String url, int connectTimeout, int readTimeout, FormFile... files) throws IOException{
        return upload(url, null ,connectTimeout , readTimeout , DEFAULT_CHARSET , files);
    }
    default String upload(String url, FormFile... files) throws IOException{
        return upload(url, null , DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_CHARSET , files);
    }

    /**
     * 上传文件和key-value数据
     * @param url URL
     * @param files 多个文件信息
     */
    String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) throws IOException;
    default String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, FormFile... files) throws IOException{
        return upload(url, params ,headers ,connectTimeout , readTimeout , DEFAULT_CHARSET , files);
    }
    default String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, FormFile... files) throws IOException{
        return upload(url, params ,headers ,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_CHARSET , files);
    }
    default String upload(String url, int connectTimeout, int readTimeout, ArrayListMultimap<String, String> params, FormFile... files) throws IOException{
        return upload(url, params ,null ,connectTimeout , readTimeout , DEFAULT_CHARSET , files);
    }
    default String upload(String url, Map<String, String> params, FormFile... files) throws IOException{
        ArrayListMultimap<String , String> multimap = ArrayListMultimap.fromMap(params);
        return upload(url, multimap ,null , DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_CHARSET , files);
    }
}
