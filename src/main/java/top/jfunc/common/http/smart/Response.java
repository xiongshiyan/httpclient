package top.jfunc.common.http.smart;


import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.http.base.HttpStatus;
import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.string.FromString;
import top.jfunc.common.string.FromStringHandler;
import top.jfunc.common.utils.ArrayUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.*;
import java.util.List;
import java.util.Objects;

/**
 * 代表请求的响应，封装statusCode、body、headers
 * 不提供对InputStream的直接支持，因为其需要手动处理资源释放
 * @author xiongshiyan at 2017/12/9
 */
public interface Response extends Closeable{
    /**
     * 获取响应码
     * @see HttpStatus
     * @return 响应码
     */
    int getStatusCode();

    /**
     * 响应的简短说明
     * @return 响应的简短说明
     */
    String getStatusPhrase();

    /**
     * 响应体作为字节数组
     * @return byte[]
     */
    byte[] getBodyAsBytes();

    /**
     * 响应体编码，可以由{@link top.jfunc.common.http.request.HttpRequest}指定，
     * 也可以由响应头获取，由实现决定
     * @return 响应体编码
     */
    String getResultCharset();

    /**
     * 响应体
     * @return 以字符串返回响应体
     */
    default String getBodyAsString(){
        try {
            byte[] bytes = getBodyAsBytes();
            if(ArrayUtil.isEmpty(bytes)){
                return "";
            }
            return new String(bytes, getResultCharset());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 此方法主要为了兼容性考虑
     * @return 响应体
     */
    default String getBody(){
        return getBodyAsString();
    }

    /**
     * 将返回结果直接转换为Java对象时使用此方法
     * @param toClass 转换的class
     * @param handler 将String转换为Java对象的策略接口
     * @return T
     */
    default <T> T as(Class<T> toClass, FromStringHandler<T> handler){
        FromStringHandler<T> stringHandler = Objects.requireNonNull(handler , "handler不能为空");
        return stringHandler.as(getBodyAsString() , toClass);
    }
    default <T> T asT(Class<T> toClass, FromString handler){
        FromString stringHandler = Objects.requireNonNull(handler , "handler不能为空");
        return stringHandler.as(getBodyAsString() , toClass);
    }

    /**
     * 建议不要使用此方法，会有效率上的折扣[InputStream->byte[]->InputStream->File]，
     * <strong>
     *     这是因为无法直接保存InputStream的引用，要么造成无法及时关闭，要么造成关闭了无法读取数据。
     * </strong>
     *
     * 提供此方法的主要目的是在既想要将内容保存为文件，
     * 又需要header等信息的时候，返回Response代表响应的所有信息。
     * 如果只需要保存为文件，那么请调用 {@link SmartHttpClient#download(DownloadRequest)}
     *
     */
    default File asFile(File fileToSave) throws IOException{
        byte[] bodyBytes = getBodyAsBytes();
        if(ArrayUtil.isEmpty(bodyBytes)){
            return fileToSave;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileToSave)){
            fileOutputStream.write(bodyBytes , 0 , bodyBytes.length);
            fileOutputStream.flush();
            return fileToSave;
        }
    }
    default File asFile(String fileName) throws IOException{
        return asFile(new File(fileName));
    }


    /**
     * 响应头，可能为空
     * @return MultiValueMap
     */
    MultiValueMap<String, String> getHeaders();

    default List<String> getHeader(String key){
        MultiValueMap<String, String> headers = getHeaders();
        if(MapUtil.isEmpty(headers)){
            return null;
        }
        return headers.get(key);
    }
    default String getFirstHeader(String key){
        MultiValueMap<String, String> headers = getHeaders();
        if(MapUtil.isEmpty(headers)){
            return null;
        }
        return headers.getFirst(key);
    }

    /**
     * 请求是否OK
     */
    default boolean isOk(){
        return HttpStatus.isOk(getStatusCode());
    }

    /**
     * 请求是否成功
     */
    default boolean isSuccess(){
        return HttpStatus.isSuccess(getStatusCode());
    }

    /**
     * 是否需要重定向
     */
    default boolean needRedirect(){
        return HttpStatus.needRedirect(getStatusCode());
    }

    /**
     * 获取重定向地址
     * @return 重定向地址
     */
    default String getRedirectUrl(){
        return this.getHeaders().getFirst(HttpHeaders.LOCATION);
    }

    @Override
    String toString();
}
