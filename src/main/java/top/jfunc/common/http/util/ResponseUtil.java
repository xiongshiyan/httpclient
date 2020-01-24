package top.jfunc.common.http.util;

import top.jfunc.common.http.response.DefaultResponse;
import top.jfunc.common.http.smart.Response;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;

/**
 * 根据响应的四要素获取想要的
 * @author xiongshiyan at 2020/1/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ResponseUtil {
    /**
     * 获取响应码
     */
    public static int extractStatusCode(int statusCode , InputStream inputStream , String resultCharset , MultiValueMap<String , String> headers) throws IOException {
        return statusCode;
    }

    /**
     * 获取响应体，String
     */
    public static String extractString(int statusCode , InputStream inputStream , String resultCharset , MultiValueMap<String , String> headers) throws IOException{
        return null != inputStream ? IoUtil.read(inputStream , resultCharset) : "";
    }

    /**
     * 获取响应体，byte[]
     */
    public static byte[] extractBytes(int statusCode , InputStream inputStream , String resultCharset , MultiValueMap<String , String> headers) throws IOException{
        return null != inputStream ? IoUtil.stream2Bytes(inputStream) : new byte[0];
    }

    /**
     * 获取响应header
     */
    public static MultiValueMap<String, String> extractHeaders(int statusCode , InputStream inputStream , String resultCharset , MultiValueMap<String , String> headers) throws IOException{
        return headers;
    }



    public static Response with(int statusCode, byte[] bodyBytes, String resultCharset, MultiValueMap<String, String> headers) {
        return new DefaultResponse(statusCode, bodyBytes, resultCharset, headers);
    }

    public static Response with(int statusCode , InputStream inputStream , String resultCharset , MultiValueMap<String , String> headers) throws IOException{
        byte[] bodyBytes = null != inputStream ? IoUtil.stream2Bytes(inputStream) : new byte[]{};
        return with(statusCode, bodyBytes, resultCharset, headers);
    }
}
