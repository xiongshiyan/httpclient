package top.jfunc.common.http.paramsign;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/8/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface ParamSigner <R>{
    /**
     * 对一个请求进行参数签名校验，校验不通过就抛出{@link ParamSignException}
     */
    void validIfNecessary(R r) throws IOException;
}
