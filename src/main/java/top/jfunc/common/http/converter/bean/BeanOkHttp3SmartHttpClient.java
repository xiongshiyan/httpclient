package top.jfunc.common.http.converter.bean;

import top.jfunc.common.converter.Converter;
import top.jfunc.common.http.smart.OkHttp3SmartHttpClient;
import top.jfunc.common.http.smart.Request;

import java.io.IOException;
import java.util.Objects;

import static top.jfunc.common.converter.Converter.checkNull;


/**
 * 返回结果String转换为 Bean
 * @author xiongshiyan at 2018/1/11
 */
public class BeanOkHttp3SmartHttpClient extends OkHttp3SmartHttpClient implements BeanSmartHttpClient {

    private Converter converter;
    public BeanOkHttp3SmartHttpClient(Converter converter){
        this.converter = Objects.requireNonNull(converter);
    }
    public BeanOkHttp3SmartHttpClient(){}

    @Override
    public BeanSmartHttpClient setConverter(Converter converter) {
        this.converter = Objects.requireNonNull(converter);
        return this;
    }

    @Override
    public <T> T get(Request request , Class<T> clazz) throws IOException {
        checkNull(this.converter);
        String result = get(request).getBody();
        return this.converter.convert(result , clazz);
    }

    @Override
    public <T> T post(Request request, Class<T> clazz) throws IOException {
        checkNull(this.converter);
        String result = post(request).getBody();
        return this.converter.convert(result , clazz);
    }
}
