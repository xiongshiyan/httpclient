package top.jfunc.common.http.request.holder;

import top.jfunc.common.http.holder.BodyHolder;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface MutableStringBodyRequest extends HttpRequest , StringBodyRequest , top.jfunc.common.http.request.MutableStringBodyRequest {
    /**
     * body的holder
     * @return bodyHolder must not be null
     */
    BodyHolder bodyHolder();

    /**
     * 获取body
     * @return body
     */
    @Override
    default String getBody(){
        return bodyHolder().getBody();
    }

    /**
     * 提供便捷方法
     * @param body body
     * @return this
     */
    @Override
    default MutableStringBodyRequest setBody(String body){
        bodyHolder().setBody(body);
        return this;
    }

    /**
     * 提供便捷方法
     * @param bodyCharset bodyCharset
     * @return this
     */
    @Override
    default MutableStringBodyRequest setBodyCharset(String bodyCharset){
        bodyHolder().setBodyCharset(bodyCharset);
        return this;
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
    @Override
    MutableStringBodyRequest setBody(String body, String contentType);
}
