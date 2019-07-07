package top.jfunc.common.http.holder;

import top.jfunc.common.string.ToString;
import top.jfunc.common.string.ToStringHandler;
import top.jfunc.common.utils.IoUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * Body处理器，Body相关的处理，提供了众多设置body的方式
 * <ul>
 *     <li>1.字符串、CharSequence</li>
 *     <li>2.Java对象，依赖于 {@link ToString} 或 {@link ToStringHandler}</li>
 *     <li>3.字节数组</li>
 *     <li>4.输入流</li>
 * </ul>
 * @author xiongshiyan
 */
public interface BodyHolder extends Holder{
    /**
     * 获取实际的Body
     * @return body
     */
    String getBody();

    /**
     * 获取body的字符编码
     * @return charset of body
     */
    String getBodyCharset();

    /**
     * 设置请求体
     * @param body body
     * @return this
     */
    BodyHolder setBody(String body);

    /**
     * 设置请求体，提供对{@link StringBuffer}、{@link StringBuilder}等{@link CharSequence}的支持
     * @param body body
     * @return this
     */
    default BodyHolder setBody(CharSequence body){
        return setBody(body.toString());
    }

    /**
     * 设置字符编码
     * @param bodyCharset 字符编码
     * @return this
     */
    BodyHolder setBodyCharset(String bodyCharset);

    /**
     * 设置字符编码
     * @param body body
     * @param bodyCharset 字符编码
     * @return this
     */
    default BodyHolder setBodyAndCharset(String body, String bodyCharset){
        return setBody(body).setBodyCharset(bodyCharset);
    }

    /**
     * 直接传输一个Java对象可以使用该方法
     * @param o Java对象
     * @param handler 将Java对象转换为String的策略接口
     * @return this
     */
    default <T> BodyHolder setBodyT(T o, ToStringHandler<T> handler){
        ToStringHandler<T> stringHandler = Objects.requireNonNull(handler, "handler不能为空");
        return setBody(stringHandler.toString(o));
    }

    /**
     * 使用{@link ToString}处理JavaObject
     * @param o Java对象
     * @param handler 处理器
     * @return this
     */
    default BodyHolder setBody(Object o, ToString handler){
        ToString toString = Objects.requireNonNull(handler, "handler不能为空");
        return setBody(toString.toString(o));
    }

    /**
     * 使用字节数组
     * @param bytes 字节数组
     * @return this
     */
    default BodyHolder setBody(byte[] bytes){
        return setBody(bytes , getBodyCharset());
    }

    /**
     * 使用字节数组
     * @param bytes 字节数组
     * @param bodyCharset 字符编码
     * @return this
     */
    default BodyHolder setBody(byte[] bytes, String bodyCharset){
        try {
            return setBodyAndCharset(new String(bytes , bodyCharset) , bodyCharset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从流中获取body，并且关闭流
     * @param inputStream 输入流
     * @param bodyCharset 编码
     * @return this
     */
    default BodyHolder setBody(InputStream inputStream, String bodyCharset){
        byte[] bytes;
        try {
            bytes = IoUtil.stream2Bytes(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            IoUtil.close(inputStream);
        }

        return setBody(bytes, bodyCharset);
    }
}
