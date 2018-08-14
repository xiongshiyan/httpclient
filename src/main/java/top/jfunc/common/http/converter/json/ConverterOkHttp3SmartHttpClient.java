package top.jfunc.common.http.converter.json;

import top.jfunc.common.converter.JsonConverter;
import top.jfunc.common.http.converter.bean.BeanOkHttp3SmartHttpClient;
import top.jfunc.common.http.smart.Request;

import java.io.IOException;
import java.util.Objects;

import static top.jfunc.common.converter.Converter.checkNull;

/**
 * 返回结果String转换为 {@link ResultConverter}
 * @author xiongshiyan at 2018/1/11
 */
public class ConverterOkHttp3SmartHttpClient extends BeanOkHttp3SmartHttpClient implements ConverterSmartHttpClient {

    private JsonConverter converter;
    public ConverterOkHttp3SmartHttpClient(JsonConverter converter){
        super(converter);
        this.converter = Objects.requireNonNull(converter);
    }
    public ConverterOkHttp3SmartHttpClient(){}

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
