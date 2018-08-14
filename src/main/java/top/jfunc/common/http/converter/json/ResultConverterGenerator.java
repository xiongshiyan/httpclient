package top.jfunc.common.http.converter.json;

import top.jfunc.common.converter.JsonConverter;
import top.jfunc.json.JsonArray;
import top.jfunc.json.JsonObject;

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
