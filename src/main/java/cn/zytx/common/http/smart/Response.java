package cn.zytx.common.http.smart;


import cn.zytx.common.http.Header;
import cn.zytx.common.http.HttpStatus;
import cn.zytx.common.utils.IoUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.zytx.common.http.HttpConstants.DEFAULT_CHARSET;

/**
 * @author xiongshiyan at 2017/12/9
 */
public class Response {
    /**
     * 返回码
     */
    private int statusCode = HttpStatus.HTTP_OK;
    /**
     * 返回体
     */
    private String body;
    /**
     * 返回体编码
     */
    private String resultCharset = DEFAULT_CHARSET;
    /**
     * 返回的header
     */
    private Map<String,List<String>> headers = new HashMap<>();

    private Response(String body) {
        this.body = body;
    }

    public static Response with(String body){
        return new Response(body);
    }
    public static Response with(String body , String resultCharset , Map<String , List<String>> headers){
        return new Response(body).setResultCharset(resultCharset).setHeaders(headers);
    }
    public static Response with(InputStream body , String resultCharset , Map<String , List<String>> headers){
        return with(HttpStatus.HTTP_OK , body , resultCharset , headers);
    }
    public static Response with(int statusCode , InputStream body , String resultCharset , Map<String , List<String>> headers){
        String read = null;
        try {
            read = IoUtil.read(body, resultCharset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Response(read).setResultCharset(resultCharset).setHeaders(headers).setStatusCode(statusCode);
    }


    public String getBody() {
        return body;
    }

    public Response setBody(String body) {
        this.body = body;
        return this;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public List<String> getHeader(String key) {
        return headers.get(key);
    }
    public String getOneHeader(String key) {
        List<String> list = headers.get(key);
        if(null == list){return null;}
        return list.get(0);
    }

    public Response addHeader(String key,String value){
        List<String> list = headers.get(key);
        if(list == null){
            list = new ArrayList<>();
            if(list.add(value)){
                headers.put(key, list);
                return this;
            } else{
                throw new AssertionError("New list violated the list spec");
            }
        } else {
            list.add(value);
            return this;
        }
    }
    public Response setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public String getResultCharset() {
        return resultCharset;
    }

    public Response setResultCharset(String resultCharset) {
        this.resultCharset = resultCharset;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Response setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * 请求是否OK
     * @return true only if statusCode==200
     */
    public boolean isOk(){
        return HttpStatus.HTTP_OK == this.statusCode;
    }

    /**
     * 是否需要重定向
     * @return true if 301|302|303
     */
    public boolean redirectable(){
        return HttpStatus.HTTP_MOVED_PERM == this.statusCode || HttpStatus.HTTP_MOVED_TEMP == this.statusCode || HttpStatus.HTTP_SEE_OTHER == this.statusCode;
    }

    /**
     * 获取重定向地址
     * @return 重定向地址
     */
    public String getRedirectUrl(){
        return this.headers.get(Header.LOCATION.name()).get(0);
    }

    @Override
    public String toString() {
        return body;
    }
}
