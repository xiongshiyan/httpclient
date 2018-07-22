package cn.zytx.common.http.withconverter.json;

import cn.zytx.common.converter.Converter;
import cn.zytx.common.converter.JsonConverter;
import cn.zytx.common.http.withconverter.ConverterNativeSmartHttpClient;
import cn.zytx.common.json.JsonArray;
import cn.zytx.common.json.JsonObject;

import java.util.Objects;

/**
 * 返回结果String转换为Json
 * @author 熊诗言2017/11/24
 */
public class JsonConverterNativeSmartHttpClient extends ConverterNativeSmartHttpClient implements JsonConverterSmartHttpClient {

    private JsonConverter converter;
    public JsonConverterNativeSmartHttpClient(JsonConverter converter){
        super(converter);
        this.converter = Objects.requireNonNull(converter);
    }
    public JsonConverterNativeSmartHttpClient(){}

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
