package cn.zytx.common.http.converter.bean;

import cn.zytx.common.converter.Converter;
import cn.zytx.common.http.smart.NativeSmartHttpClient;
import cn.zytx.common.http.smart.Request;

import java.io.IOException;
import java.util.Objects;

import static cn.zytx.common.converter.Converter.checkNull;

/**
 * 返回结果String转换为 Bean
 * @author 熊诗言2017/11/24
 */
public class BeanNativeSmartHttpClient extends NativeSmartHttpClient implements BeanSmartHttpClient {

    private Converter converter;
    public BeanNativeSmartHttpClient(Converter converter){
        this.converter = Objects.requireNonNull(converter);
    }
    public BeanNativeSmartHttpClient(){}

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
