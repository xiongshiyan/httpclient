package top.jfunc.common.http.request;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface ChangeableStringBodyRequest extends StringBodyRequest {
    /**
     * 设置请求体
     * @param body body
     * @return this
     */
    ChangeableStringBodyRequest setBody(String body);
}
