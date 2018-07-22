package cn.zytx.common.http.withconverter.json;

import cn.zytx.common.converter.Converter;
import cn.zytx.common.converter.JsonConverter;
import cn.zytx.common.http.withconverter.ConverterApacheSmartHttpClient;
import cn.zytx.common.json.JsonArray;
import cn.zytx.common.json.JsonObject;

import java.util.Objects;

/**
 * 返回结果String转换为Json
 * @author 熊诗言2017/12/01
 */
public class JsonConverterApacheSmartHttpClient extends ConverterApacheSmartHttpClient implements JsonConverterSmartHttpClient {

    private JsonConverter converter;
    public JsonConverterApacheSmartHttpClient(JsonConverter converter){
        super(converter);
        this.converter = Objects.requireNonNull(converter);
    }
    public JsonConverterApacheSmartHttpClient(){}

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
