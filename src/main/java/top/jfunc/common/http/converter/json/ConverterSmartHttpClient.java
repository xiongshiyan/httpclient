package top.jfunc.common.http.converter.json;

import top.jfunc.common.converter.JsonConverter;
import top.jfunc.common.http.converter.bean.BeanSmartHttpClient;
import top.jfunc.common.http.smart.Request;

import java.io.IOException;

/**
 * @author xiongshiyan at 2017/12/9
 * 对结果集进行转换
 */
public interface ConverterSmartHttpClient extends BeanSmartHttpClient {

    ConverterSmartHttpClient setConverter(JsonConverter converter);

    /**
     * get请求并对结果生成一个结果转换器
     */
    ResultConverter getAndConvert(Request request) throws IOException;
    /**
     * post请求并对结果生成一个结果转换器
     */
    ResultConverter postAndConvert(Request request) throws IOException;
}
