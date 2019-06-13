package top.jfunc.common.http.request;

import top.jfunc.common.http.holder.BodyHolder;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface MutableStringBodyRequest<T extends MutableStringBodyRequest> extends StringBodyRequest<T> {

    /**
     * 获取body
     * @return body
     */
    @Override
    default String getBody(){
        return bodyHolder().getBody();
    }

    /**
     * body的holder
     * @return bodyHolder must not be null
     */
    BodyHolder bodyHolder();

    /**
     * 提供便捷方法
     * @param body body
     * @return this
     */
    default T setBody(String body){
        bodyHolder().setBody(body);
        return myself();
    }

    /**
     * 提供便捷方法
     * @param bodyCharset bodyCharset
     * @return this
     */
    default T setBodyCharset(String bodyCharset){
        bodyHolder().setBodyCharset(bodyCharset);
        return myself();
    }

    /**
     * 获取请求体编码
     * @return charset
     */
    @Override
    default String getBodyCharset(){
        return bodyHolder().getBodyCharset();
    }

    /**
     * 设置请求体
     * @param body body
     * @param contentType Content-Type
     * @return this
     */
    T setBody(String body, String contentType);
}
