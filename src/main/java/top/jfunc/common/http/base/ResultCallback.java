package top.jfunc.common.http.base;

import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;

/**
 * 结果封装，更通用的封装方式，输入更通用，自己转化成需要的数据结构string、bytes、file...
 * @author xiongshiyan at 2018/6/7
 */
@FunctionalInterface
public interface ResultCallback<R> {
    /**
     * 转换结果
     * @param statusCode 返回码
     * @param inputStream body，代表输入流，自己转化成需要的数据结构string、bytes、file...
     * @param resultCharset 编码
     * @param headers headers
     * @return <R>
     * @throws IOException IOException
     * @see top.jfunc.common.http.smart.Response
     */
    R convert(int statusCode, InputStream inputStream, String resultCharset, MultiValueMap<String, String> headers) throws IOException;
}
