package cn.zytx.common.http.withconverter;

import cn.zytx.common.converter.Converter;
import cn.zytx.common.http.smart.NativeSmartHttpClient;

import java.util.Objects;

/**
 * 返回结果String转换为<R>
 * @author 熊诗言2017/11/24
 */
public class ConverterNativeSmartHttpClient extends NativeSmartHttpClient implements ConverterSmartHttpClient {

    private Converter converter;
    public ConverterNativeSmartHttpClient(Converter converter){
        this.converter = Objects.requireNonNull(converter);
    }
    public ConverterNativeSmartHttpClient(){}

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
