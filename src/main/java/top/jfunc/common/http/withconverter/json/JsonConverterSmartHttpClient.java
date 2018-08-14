package top.jfunc.common.http.withconverter.json;

import top.jfunc.common.converter.JsonConverter;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.basic.HttpClient;
import top.jfunc.common.http.smart.Request;
import top.jfunc.common.http.smart.Response;
import top.jfunc.common.http.withconverter.ConverterSmartHttpClient;
import top.jfunc.json.JsonArray;
import top.jfunc.json.JsonObject;

import java.io.IOException;
import java.util.Map;

import static top.jfunc.common.http.HttpConstants.*;

/**
 * @author xiongshiyan at 2018/7/16
 * 返回的结果值直接封装为json相关
 */
public interface JsonConverterSmartHttpClient extends ConverterSmartHttpClient, JsonConverter {
    JsonConverterSmartHttpClient setConverter(JsonConverter converter);

    default JsonObject getJsonObject(String url, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout, String resultCharset) throws IOException{
        String result = get(url, params, headers, connectTimeout, readTimeout, resultCharset);
        return convertJsonObject(result);
    }
    default JsonObject getJsonObject(String url, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout) throws IOException{
        return getJsonObject(url, params, headers, connectTimeout, readTimeout , DEFAULT_CHARSET);
    }
    default JsonObject getJsonObject(String url, Map<String, String> params, Map<String, String> headers, String resultCharset) throws IOException{
        return getJsonObject(url,params,headers,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT,resultCharset);
    }
    default JsonObject getJsonObject(String url, Map<String, String> params, Map<String, String> headers) throws IOException{
        return getJsonObject(url,params,headers,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }
    default JsonObject getJsonObject(String url, Map<String, String> params, int connectTimeout, int readTimeout, String resultCharset) throws IOException{
        return getJsonObject(url,params,null,connectTimeout,readTimeout,resultCharset);
    }
    default JsonObject getJsonObject(String url, Map<String, String> params, int connectTimeout, int readTimeout) throws IOException{
        return getJsonObject(url,params,null,connectTimeout,readTimeout);
    }
    default JsonObject getJsonObject(String url, Map<String, String> params, String resultCharset) throws IOException{
        return getJsonObject(url,params,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT,resultCharset);
    }
    default JsonObject getJsonObject(String url, Map<String, String> params) throws IOException{
        return getJsonObject(url,params,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }
    default JsonObject getJsonObject(String url, String resultCharset) throws IOException{
        return getJsonObject(url,null,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, resultCharset);
    }
    default JsonObject getJsonObject(String url) throws IOException{
        return getJsonObject(url,null,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }


    default JsonObject post2JsonObject(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) throws IOException{
        String result = post(url, body, contentType, headers, connectTimeout, readTimeout, bodyCharset, resultCharset);
        return convertJsonObject(result);
    }
    default JsonObject  post2JsonObject(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout) throws IOException{
        return post2JsonObject(url,body,contentType,headers,connectTimeout,readTimeout, DEFAULT_CHARSET,DEFAULT_CHARSET);
    }
    default JsonObject  post2JsonObject(String url, String body, String contentType, Map<String, String> headers, String bodyCharset, String resultCharset) throws IOException{
        return post2JsonObject(url,body,contentType,headers,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT,bodyCharset,resultCharset);
    }
    default JsonObject  post2JsonObject(String url, String body, String contentType, Map<String, String> headers) throws IOException{
        return post2JsonObject(url,body,contentType,headers,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }
    default JsonObject  post2JsonObject(String url, String body, String contentType, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) throws IOException{
        return post2JsonObject(url,body,contentType,null,connectTimeout,readTimeout,bodyCharset,resultCharset);
    }
    default JsonObject  post2JsonObject(String url, String body, String contentType, int connectTimeout, int readTimeout) throws IOException{
        return post2JsonObject(url,body,contentType,null,connectTimeout,readTimeout);
    }
    /**
     * @see HttpClient#post(String, String, String, Map, int, int, String, String)
     */
    default JsonObject  post2JsonObject(String url, String body, String contentType, String bodyCharset, String resultCharset) throws IOException{
        return post2JsonObject(url,body,contentType,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT,bodyCharset,resultCharset);
    }
    default JsonObject  post2JsonObject(String url, String body, String contentType) throws IOException{
        return post2JsonObject(url,body,contentType,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }
    default JsonObject  postJson2JsonObject(String url, String body, String bodyCharset, String resultCharset) throws IOException{
        return post2JsonObject(url,body,JSON_WITH_DEFAULT_CHARSET,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT,bodyCharset,resultCharset);
    }
    default JsonObject  postJson2JsonObject(String url, String body) throws IOException{
        return post2JsonObject(url,body,JSON_WITH_DEFAULT_CHARSET,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**参数用 =和& 连接*/
    default JsonObject  post2JsonObject(String url, Map<String, String> params, Map<String, String> headers, String bodyCharset, String resultCharset) throws IOException{
        return post2JsonObject(url, ParamUtil.contactMap(params , bodyCharset),FORM_URLENCODED_WITH_DEFAULT_CHARSET,headers,bodyCharset,resultCharset);
    }
    default JsonObject  post2JsonObject(String url, Map<String, String> params, Map<String, String> headers) throws IOException{
        return post2JsonObject(url, ParamUtil.contactMap(params , DEFAULT_CHARSET),FORM_URLENCODED_WITH_DEFAULT_CHARSET,headers);
    }
    default JsonObject  post2JsonObject(String url, Map<String, String> params, String bodyCharset, String resultCharset) throws IOException{
        return post2JsonObject(url, ParamUtil.contactMap(params , bodyCharset),FORM_URLENCODED_WITH_DEFAULT_CHARSET,null,bodyCharset,resultCharset);
    }
    default JsonObject  post2JsonObject(String url, Map<String, String> params) throws IOException{
        return post2JsonObject(url, ParamUtil.contactMap(params , DEFAULT_CHARSET),FORM_URLENCODED_WITH_DEFAULT_CHARSET,null);
    }

    default JsonObject  get2JsonObject(Request request) throws IOException{
        Response response = get(request);
        return convertJsonObject(response.getBody());
    }
    default JsonObject  post2JsonObject(Request request) throws IOException{
        Response response = post(request);
        return convertJsonObject(response.getBody());
    }
    default JsonArray get2JsonArray(Request request) throws IOException{
        Response response = get(request);
        return convertJsonArray(response.getBody());
    }
    default JsonArray  post2JsonArray(Request request) throws IOException{
        Response response = post(request);
        return convertJsonArray(response.getBody());
    }
}
