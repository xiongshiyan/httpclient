package top.jfunc.common.http.component;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.util.Map;

/**
 * @author xiongshiyan at 2020/1/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface AssemblingFactory {
    /**
     * 根据给定的相关参数组装为一个{@link HttpRequest}，支持{@link top.jfunc.common.http.base.Method#GET}等没有body的请求
     * @see top.jfunc.common.http.smart.AbstractSmartHttpClient#get(String, Map, Map, int, int, String)
     * @see top.jfunc.common.http.smart.AbstractSmartHttpClient#getAsBytes(String, MultiValueMap, int, int)
     * @see top.jfunc.common.http.smart.AbstractSmartHttpClient#getAsFile(String, MultiValueMap, File, int, int)
     * @param url URL
     * @param queryParams 查询参数 maybe null
     * @param headers header maybe null
     * @param connectTimeout 连接超时
     * @param readTimeout 读取超时
     * @param resultCharset 结果字符集 maybe null
     * @return HttpRequest HttpRequest
     */
    HttpRequest create(String url,
                       MultiValueMap<String, String> queryParams,
                       MultiValueMap<String, String> headers,
                       int connectTimeout,
                       int readTimeout,
                       String resultCharset);

    /**
     * 根据给定的相关参数组装为一个{@link StringBodyRequest}，
     * 支持{@link top.jfunc.common.http.base.Method#POST}等含有body的请求
     * @see top.jfunc.common.http.smart.AbstractSmartHttpClient#post(String, String, String, Map, int, int, String, String)
     * @param url URL
     * @param queryParams 查询参数 maybe null
     * @param body body
     * @param contentType ContentType
     * @param headers header maybe null
     * @param connectTimeout 连接超时
     * @param readTimeout 读取超时
     * @param bodyCharset 字符集 maybe null
     * @param resultCharset 结果字符集 maybe null
     * @return HttpRequest HttpRequest
     */
    StringBodyRequest create(String url,
                             MultiValueMap<String, String> queryParams,
                             String body,
                             String contentType,
                             MultiValueMap<String, String> headers,
                             int connectTimeout,
                             int readTimeout,
                             String bodyCharset,
                             String resultCharset);

    /**
     * 根据给定的相关参数组装为一个{@link UploadRequest}，支持{@link top.jfunc.common.http.base.Method#POST}
     * @see top.jfunc.common.http.smart.AbstractSmartHttpClient#upload(String, MultiValueMap, MultiValueMap, int, int, String, FormFile...)
     * @param url URL
     * @param formParams Form参数 maybe null
     * @param headers header maybe null
     * @param connectTimeout 连接超时
     * @param readTimeout 读取超时
     * @param resultCharset 结果字符集 maybe null
     * @param formFiles Form文件
     * @return UploadRequest UploadRequest
     */
    UploadRequest create(String url,
                         MultiValueMap<String, String> formParams,
                         FormFile[] formFiles,
                         MultiValueMap<String, String> headers,
                         int connectTimeout,
                         int readTimeout,
                         String resultCharset);
}
