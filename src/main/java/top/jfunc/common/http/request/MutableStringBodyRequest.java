package top.jfunc.common.http.request;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface MutableStringBodyRequest extends StringBodyRequest {
    /**
     * 提供便捷方法
     * @param body body
     * @return this
     */
    MutableStringBodyRequest setBody(String body);

    /**
     * 设置请求体
     * @param body body
     * @param contentType Content-Type
     * @return this
     */
    MutableStringBodyRequest setBody(String body, String contentType);
}
