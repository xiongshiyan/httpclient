package top.jfunc.common.http.withconverter.json;

import top.jfunc.common.converter.Converter;
import top.jfunc.common.converter.JsonConverter;
import top.jfunc.common.http.withconverter.ConverterOkHttp3SmartHttpClient;
import top.jfunc.json.JsonArray;
import top.jfunc.json.JsonObject;

import java.util.Objects;

/**
 * 返回结果String转换为Json
 * @author xiongshiyan at 2018/1/11
 */
public class JsonConverterOkHttp3SmartHttpClient extends ConverterOkHttp3SmartHttpClient implements JsonConverterSmartHttpClient {

    private JsonConverter converter;
    public JsonConverterOkHttp3SmartHttpClient(JsonConverter converter){
        super(converter);
    }
    public JsonConverterOkHttp3SmartHttpClient(){}

    @Override
    public JsonConverterSmartHttpClient setConverter(JsonConverter converter) {
        super.setConverter(converter);
        this.converter = Objects.requireNonNull(converter);
        return this;
    }

    @Override
    public JsonObject convertJsonObject(String src) {
        Converter.checkNull(this.converter);
        return this.converter.convertJsonObject(src);
    }

    @Override
    public JsonArray convertJsonArray(String src) {
        Converter.checkNull(this.converter);
        return this.converter.convertJsonArray(src);
    }
}
