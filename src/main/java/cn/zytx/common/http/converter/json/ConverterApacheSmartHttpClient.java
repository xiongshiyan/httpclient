package cn.zytx.common.http.converter.json;

import cn.zytx.common.converter.JsonConverter;
import cn.zytx.common.http.converter.bean.BeanApacheSmartHttpClient;
import cn.zytx.common.http.smart.Request;

import java.io.IOException;
import java.util.Objects;

import static cn.zytx.common.converter.Converter.checkNull;

/**
 * 返回结果String转换为 {@link ResultConverter}
 * @author 熊诗言2017/12/01
 */
public class ConverterApacheSmartHttpClient extends BeanApacheSmartHttpClient implements ConverterSmartHttpClient {

    private JsonConverter converter;
    public ConverterApacheSmartHttpClient(JsonConverter converter){
        super(converter);
        this.converter = Objects.requireNonNull(converter);
    }
    public ConverterApacheSmartHttpClient(){}

    @Override
    public ConverterSmartHttpClient setConverter(JsonConverter converter) {
        this.converter = Objects.requireNonNull(converter);
        super.setConverter(converter);
        return this;
    }

    @Override
    public ResultConverter getAndConvert(Request request) throws IOException {
        checkNull(this.converter);
        String result = get(request).getBody();
        return ResultConverterGenerator.generate(result , this.converter);
    }

    @Override
    public ResultConverter postAndConvert(Request request) throws IOException {
        checkNull(this.converter);
        String result = post(request).getBody();
        return ResultConverterGenerator.generate(result , this.converter);
    }
}
