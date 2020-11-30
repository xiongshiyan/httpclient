package top.jfunc.common.http.response;

import top.jfunc.common.http.smart.Response;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;

/**
 * 根据响应的四要素获取想要的
 * @author xiongshiyan at 2020/1/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ResponseExtractor {
    /**
     * 获取响应码
     */
    public static int extractStatusCode(ClientHttpResponse clientHttpResponse, String resultCharset) throws IOException {
        return clientHttpResponse.getStatusCode();
    }

    /**
     * 获取响应header
     */
    public static MultiValueMap<String, String> extractHeaders(ClientHttpResponse clientHttpResponse, String resultCharset) throws IOException {
        return clientHttpResponse.getHeaders();
    }
    /**
     * 获取响应体，String
     */
    public static String extractString(ClientHttpResponse clientHttpResponse, String resultCharset) throws IOException{
        InputStream inputStream = clientHttpResponse.getBody();
        return null != inputStream ? IoUtil.read(inputStream , resultCharset) : "";
    }

    /**
     * 获取响应体，byte[]
     */
    public static byte[] extractBytes(ClientHttpResponse clientHttpResponse, String resultCharset) throws IOException{
        InputStream inputStream = clientHttpResponse.getBody();
        return null != inputStream ? IoUtil.stream2Bytes(inputStream) : new byte[0];
    }

    public static Response toResponse(ClientHttpResponse clientHttpResponse, String resultCharset) throws IOException{
        byte[] bodyBytes = extractBytes(clientHttpResponse, resultCharset);
        return new DefaultResponse(clientHttpResponse.getStatusCode(), clientHttpResponse.getStatusText(), bodyBytes, resultCharset, clientHttpResponse.getHeaders());
    }
}
