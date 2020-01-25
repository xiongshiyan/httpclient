package top.jfunc.common.http.response;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.base.HttpStatus;
import top.jfunc.common.http.smart.Response;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.util.List;

/**
 * @author xiongshiyan at 2020/1/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultResponse implements Response{
    /**
     * 返回码
     */
    private int statusCode = HttpStatus.HTTP_OK;
    /**
     * 返回体的字节数组
     */
    private byte[] bodyBytes;
    /**
     * 返回体编码
     */
    private String resultCharset = HttpConstants.DEFAULT_CHARSET;
    /**
     * 返回的header
     */
    private MultiValueMap<String, String> headers = null;

    public DefaultResponse(int statusCode, byte[] bodyBytes, String resultCharset, MultiValueMap<String, String> headers) {
        this.statusCode = statusCode;
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
    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public List<String> getHeader(String key) {
        if(MapUtil.isEmpty(headers)){
            return null;
        }
        return headers.get(key);
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
    public String toString() {
        return getBodyAsString();
    }

    @Override
    public void close() throws IOException {
        //release
        this.bodyBytes = null;
        this.headers = null;
    }
}
