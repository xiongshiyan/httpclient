package top.jfunc.common.http.withconverter;

import top.jfunc.common.converter.Converter;
import top.jfunc.common.http.smart.OkHttp3SmartHttpClient;

import java.util.Objects;

/**
 * 返回结果String转换为<R>
 * @author xiongshiyan at 2018/1/11
 */
public class ConverterOkHttp3SmartHttpClient extends OkHttp3SmartHttpClient implements ConverterSmartHttpClient {

    private Converter converter;
    public ConverterOkHttp3SmartHttpClient(Converter converter){
        this.converter = Objects.requireNonNull(converter);
    }
    public ConverterOkHttp3SmartHttpClient(){}

    @Override
    public ConverterSmartHttpClient setConverter(Converter converter) {
        this.converter = Objects.requireNonNull(converter);
        return this;
    }

    @Override
    public <R> R convert(String src, Class<R> clazz) {
        Converter.checkNull(this.converter);
        return converter.convert(src , clazz);
    }
}
