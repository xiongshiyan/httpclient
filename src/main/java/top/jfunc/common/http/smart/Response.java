package top.jfunc.common.http.smart;


import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.HttpStatus;
import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.string.FromString;
import top.jfunc.common.string.FromStringHandler;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.*;
import java.util.List;
import java.util.Objects;

/**
 * 代表请求的响应，封装statusCode、body、headers
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
    private String resultCharset = HttpConstants.DEFAULT_CHARSET;
    /**
     * 返回的header
     */
    private MultiValueMap<String, String> headers = null;

    private Response(int statusCode, byte[] bodyBytes, String resultCharset, MultiValueMap<String, String> headers) {
        this.statusCode = statusCode;
        this.bodyBytes = bodyBytes;
        this.resultCharset = resultCharset;
        this.headers = headers;
    }

    public static Response with(int statusCode, byte[] bodyBytes, String resultCharset, MultiValueMap<String, String> headers) {
        return new Response(statusCode, bodyBytes, resultCharset, headers);
    }

    public static Response with(int statusCode , InputStream inputStream , String resultCharset , MultiValueMap<String , String> headers) throws IOException{
        return new Response(statusCode ,
                null == inputStream ? new byte[]{} : IoUtil.stream2Bytes(inputStream) ,
                resultCharset ,
                headers);
    }

    /**
     * 获取响应码
     */
    public static int extractStatusCode(int statusCode , InputStream inputStream , String resultCharset , MultiValueMap<String , String> headers) throws IOException{
        return statusCode;
    }

    /**
     * 获取响应体，String
     */
    public static String extractString(int statusCode , InputStream inputStream , String resultCharset , MultiValueMap<String , String> headers) throws IOException{
        return IoUtil.read(null != inputStream ? inputStream : new ByteArrayInputStream(new byte[0]), resultCharset);
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
     * 如果只需要保存为文件，那么请调用 {@link SmartHttpClient#getAsFile(DownloadRequest)}
     *
     */
    public File asFile(File fileToSave){
        try {
            return IoUtil.copy2File(new ByteArrayInputStream(bodyBytes), fileToSave);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    /**
     * 可能为空
     */
    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    public List<String> getHeader(String key) {
        if(MapUtil.isEmpty(headers)){
            return null;
        }
        return headers.get(key);
    }
    public String getOneHeader(String key) {
        return getFirstHeader(key);
    }
    public String getFirstHeader(String key) {
        if(MapUtil.isEmpty(headers)){
            return null;
        }
        return headers.getFirst(key);
    }

    public String getResultCharset() {
        return resultCharset;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * 请求是否OK
     */
    public boolean isOk(){
        return HttpStatus.isOk(statusCode);
    }

    /**
     * 请求是否成功
     */
    public boolean isSuccess(){
        return HttpStatus.isSuccess(statusCode);
    }

    /**
     * 是否需要重定向
     */
    public boolean needRedirect(){
        return HttpStatus.needRedirect(statusCode);
    }

    /**
     * 获取重定向地址
     * @return 重定向地址
     */
    public String getRedirectUrl(){
        return this.headers.get(HttpHeaders.LOCATION).get(0);
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
