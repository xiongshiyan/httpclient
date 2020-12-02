package top.jfunc.common.http.response;

import java.io.IOException;

/**
 * 结果封装，更通用的封装方式，输入更通用，自己转化成需要的数据结构string、bytes、file...
 * 必须立即使用，因为框架会自动关闭{@link ClientHttpResponse}
 * 常见的几个要素用{@link top.jfunc.common.http.util.ResponseExtractor}即可获取
 * @author xiongshiyan at 2020/11/30 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@FunctionalInterface
public interface ClientHttpResponseConverter<R> {
    /**
     * 将响应转换为需要的
     * @param clientHttpResponse ClientHttpResponse
     * @param resultCharset 字符集
     * @return R what you want
     * @throws IOException IOException
     */
    R convert(ClientHttpResponse clientHttpResponse, String resultCharset) throws IOException;
}
