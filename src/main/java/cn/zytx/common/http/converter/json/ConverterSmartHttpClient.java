package cn.zytx.common.http.converter.json;

import cn.zytx.common.converter.JsonConverter;
import cn.zytx.common.http.converter.bean.BeanSmartHttpClient;
import cn.zytx.common.http.smart.Request;

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
