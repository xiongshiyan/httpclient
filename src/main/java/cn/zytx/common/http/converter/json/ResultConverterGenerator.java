package cn.zytx.common.http.converter.json;

import cn.zytx.common.converter.JsonConverter;
import cn.zytx.common.json.JsonArray;
import cn.zytx.common.json.JsonObject;

class ResultConverterGenerator {
    static ResultConverter generate(final String result , final JsonConverter converter){
        return new ResultConverter() {
            @Override
            public <T> T toBean(Class<T> clazz) {
                return converter.convert(result, clazz);
            }

            @Override
            public JsonObject toJsonObject() {
                return converter.convertJsonObject(result);
            }

            @Override
            public JsonArray toJsonArray() {
                return converter.convertJsonArray(result);
            }
        };
    }
}
