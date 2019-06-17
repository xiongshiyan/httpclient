package top.jfunc.common.http.holder;

import top.jfunc.common.http.base.handler.ToString;
import top.jfunc.common.http.base.handler.ToStringHandler;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * Body处理器，Body相关的处理
 * @author xiongshiyan
 */
public interface BodyHolder {
    /**
     * 获取实际的Body
     * @return body
     */
    String getBody();

    /**
     * 获取body的字符编码
     * @return this
     */
    String getBodyCharset();

    /**
     * 设置请求体
     * @param body body
     * @return this
     */
    BodyHolder setBody(String body);

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
}
