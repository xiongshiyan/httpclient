package cn.zytx.common.http.converter.bean;

import cn.zytx.common.converter.Converter;
import cn.zytx.common.http.smart.Request;
import cn.zytx.common.http.smart.SmartHttpClient;

import java.io.IOException;

/**
 * @author xiongshiyan at 2017/12/9
 * 对结果集进行转换
 */
public interface BeanSmartHttpClient extends SmartHttpClient{

    BeanSmartHttpClient setConverter(Converter converter);

    /**
     * get请求并对结果生成一个结果转换器
     */
    <T> T get(Request request, Class<T> clazz) throws IOException;
    /**
     * post请求并对结果生成一个结果转换器
     */
    <T> T post(Request request, Class<T> clazz) throws IOException;
}
