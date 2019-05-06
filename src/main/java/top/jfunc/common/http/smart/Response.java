package top.jfunc.common.http.smart;


import top.jfunc.common.http.Header;
import top.jfunc.common.http.HttpStatus;
import top.jfunc.common.http.base.handler.FromString;
import top.jfunc.common.http.base.handler.FromStringHandler;
import top.jfunc.common.utils.IoUtil;

import java.io.*;
import java.util.*;

import static top.jfunc.common.http.HttpConstants.DEFAULT_CHARSET;

/**
 * @author xiongshiyan at 2017/12/9
 */
public class Response implements Closeable{
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
    private String resultCharset = DEFAULT_CHARSET;
    /**
     * 返回的header
     */
    private Map<String,List<String>> headers = new HashMap<>();

    private Response(int statusCode, byte[] bodyBytes, String resultCharset, Map<String, List<String>> headers) {
        this.statusCode = statusCode;
        this.bodyBytes = bodyBytes;
        this.resultCharset = resultCharset;
        this.headers = headers;
    }

    ///

    /*private Response(String body) {
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
    }*/

    public static Response with(int statusCode , InputStream inputStream , String resultCharset , Map<String , List<String>> headers){
        ///
        /*String read = null;
        try {
            read = IoUtil.read(inputStream, resultCharset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        try {
            return new Response(statusCode ,
                    IoUtil.stream2Bytes(inputStream) ,
                    resultCharset ,
                    headers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] asBytes() {
        return this.bodyBytes;
    }

    public String asString(){
        try {
            return new String(bodyBytes , resultCharset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBody() {
        return asString();
    }

    /**
     * 将返回结果直接转换为Java对象时使用此方法
     * @param toClass 转换的class
     * @param handler 将String转换为Java对象的策略接口
     * @return T
     */
    public <T> T as(Class<T> toClass , FromStringHandler<T> handler){
        FromStringHandler<T> stringHandler = Objects.requireNonNull(handler , "handler不能为空");
        return stringHandler.as(asString() , toClass);
    }
    public <T> T asT(Class<T> toClass , FromString handler){
        FromString stringHandler = Objects.requireNonNull(handler , "handler不能为空");
        return stringHandler.as(asString() , toClass);
    }

    public File asFile(String fileName){
        return asFile(new File(fileName));
    }
    /**
     * 建议不要使用此方法，会有效率上的折扣[InputStream->byte[]->InputStream->File]，
     * <strong>
     *     这是因为无法直接保存InputStream的引用，要么造成无法及时关闭，要么造成关闭了无法读取数据。
     * </strong>
     *
     * 提供此方法的主要目的是在既想要将内容保存为文件，
     * 又需要header等信息的时候，返回Response代表响应的所有信息。
     * 如果只需要保存为文件，那么请调用 {@link SmartHttpClient#getAsFile(Request)}
     *
     */
    public File asFile(File fileToSave){
        try {
            return IoUtil.copy2File(new ByteArrayInputStream(bodyBytes), fileToSave);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
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

    public String getResultCharset() {
        return resultCharset;
    }

    public int getStatusCode() {
        return statusCode;
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
    public boolean needRedirect(){
        return HttpStatus.HTTP_MOVED_PERM == this.statusCode
                || HttpStatus.HTTP_MOVED_TEMP == this.statusCode
                || HttpStatus.HTTP_SEE_OTHER == this.statusCode;
    }

    /**
     * 获取重定向地址
     * @return 重定向地址
     */
    public String getRedirectUrl(){
        return this.headers.get(Header.LOCATION.toString()).get(0);
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public void close() throws IOException {
        //release
        this.bodyBytes = null;
        this.headers = null;
    }
}
