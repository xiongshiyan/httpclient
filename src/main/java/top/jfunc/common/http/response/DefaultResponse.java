package top.jfunc.common.http.response;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.HttpStatus;
import top.jfunc.common.http.smart.Response;
import top.jfunc.common.utils.ArrayUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author xiongshiyan at 2020/1/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultResponse implements Response{
    /**
     * 返回码
     */
    private int statusCode = HttpStatus.HTTP_OK;
    private String statusPhrase;
    /**
     * 返回体的字节数组
     */
    private byte[] bodyBytes;
    /**
     * 缓存响应字符串
     * @see DefaultResponse#getBodyAsString()
     * @see DefaultResponse#bodyBytes
     */
    private String cacheString;
    /**
     * 返回体编码
     */
    private String resultCharset = Config.DEFAULT_CHARSET;
    /**
     * 返回的header
     */
    private MultiValueMap<String, String> headers = null;

    public DefaultResponse(int statusCode, String statusPhrase, byte[] bodyBytes, String resultCharset, MultiValueMap<String, String> headers) {
        this.statusCode = statusCode;
        this.statusPhrase = statusPhrase;
        this.bodyBytes = bodyBytes;
        if(null != resultCharset){
            this.resultCharset = resultCharset;
        }
        this.headers = headers;
    }

    @Override
    public byte[] getBodyAsBytes() {
        return this.bodyBytes;
    }

    @Override
    public String getBodyAsString() {
        if(null != cacheString){
            return cacheString;
        }
        try {
            byte[] bytes = getBodyAsBytes();
            if(ArrayUtil.isEmpty(bytes)){
                return cacheString = "";
            }
            return cacheString = new String(bytes, getResultCharset());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getResultCharset() {
        return resultCharset;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusPhrase() {
        return statusPhrase;
    }

    @Override
    public String toString() {
        return getBodyAsString();
    }

    @Override
    public void close() throws IOException {
        //release
        this.bodyBytes   = null;
        this.cacheString = null;
        this.headers     = null;
    }
}
