package top.jfunc.common.http.holder;

import top.jfunc.common.utils.CharsetUtil;

/**
 * 默认的基于String的实现，如果字节场景较多，可以基于字节实现
 * @author xiongshiyan at 2019/6/13 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultBodyHolder implements BodyHolder {
    private String body;
    private String bodyCharset = CharsetUtil.UTF_8;

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public BodyHolder setBody(String body) {
        this.body = body;
        return this;
    }

    @Override
    public String getBodyCharset() {
        return bodyCharset;
    }

    @Override
    public BodyHolder setBodyCharset(String bodyCharset) {
        this.bodyCharset = bodyCharset;
        return this;
    }
}
