package top.jfunc.common.http.request;

/**
 * 有请求体的请求
 * !!因为Form请求体也是Body，但是用setBody方式不好使用，所以两个分开
 *
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

    /**
     * 设置BodyCharset
     * 默认都是{@link top.jfunc.common.http.base.Config#DEFAULT_CHARSET}，如果不是
     * 需要显式地设置为null才能利用{@link top.jfunc.common.http.base.Config#defaultBodyCharset}
     * @see top.jfunc.common.http.base.Config#calculateBodyCharset(String, String)
     * @param bodyCharset bodyCharset
     * @return this
     */
    StringBodyRequest setBodyCharset(String bodyCharset);
}
