package top.jfunc.common.http.component.assembling;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.utils.MultiValueMap;

/**
 * @author xiongshiyan at 2020/1/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface UploadRequestFactory{
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
