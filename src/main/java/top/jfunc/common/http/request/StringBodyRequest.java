package top.jfunc.common.http.request;

/**
 * 有请求体的请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface StringBodyRequest extends HttpRequest {
    /**
     * 请求体，可以代表Form、Json等
     * @return 请求体
     */
    String getBody();

    /**
     * 获取body的编码
     * @return charset
     */
    String getBodyCharset();
}
