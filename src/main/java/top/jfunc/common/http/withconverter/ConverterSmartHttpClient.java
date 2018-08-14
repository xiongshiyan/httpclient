package top.jfunc.common.http.withconverter;

import top.jfunc.common.converter.Converter;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.basic.HttpClient;
import top.jfunc.common.http.smart.Request;
import top.jfunc.common.http.smart.Response;
import top.jfunc.common.http.smart.SmartHttpClient;

import java.io.IOException;
import java.util.Map;

import static top.jfunc.common.http.HttpConstants.*;

/**
 * @author xiongshiyan at 2018/7/16
 * 返回的结果值直接封装为bean
 */
public interface ConverterSmartHttpClient extends SmartHttpClient , Converter {
    ConverterSmartHttpClient setConverter(Converter converter);

    default <T> T get(String url, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout, String resultCharset, Class<T> clazz) throws IOException{
        String result = get(url, params, headers, connectTimeout, readTimeout, resultCharset);
        return convert(result , clazz);
    }
    default <T> T get(String url, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout, Class<T> clazz) throws IOException{
        return get(url, params, headers, connectTimeout, readTimeout , DEFAULT_CHARSET , clazz);
    }
    default <T> T get(String url, Map<String, String> params, Map<String, String> headers, String resultCharset, Class<T> clazz) throws IOException{
        return get(url,params,headers,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT,resultCharset , clazz);
    }
    default <T> T get(String url, Map<String, String> params, Map<String, String> headers, Class<T> clazz) throws IOException{
        return get(url,params,headers,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, clazz);
    }
    default <T> T get(String url, Map<String, String> params, int connectTimeout, int readTimeout, String resultCharset, Class<T> clazz) throws IOException{
        return get(url,params,null,connectTimeout,readTimeout,resultCharset , clazz);
    }
    default <T> T get(String url, Map<String, String> params, int connectTimeout, int readTimeout, Class<T> clazz) throws IOException{
        return get(url,params,null,connectTimeout,readTimeout , clazz);
    }
    default <T> T get(String url, Map<String, String> params, String resultCharset, Class<T> clazz) throws IOException{
        return get(url,params,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT,resultCharset , clazz);
    }
    default <T> T get(String url, Map<String, String> params, Class<T> clazz) throws IOException{
        return get(url,params,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, clazz);
    }
    default <T> T get(String url, String resultCharset, Class<T> clazz) throws IOException{
        return get(url,null,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, resultCharset , clazz);
    }
    default <T> T get(String url, Class<T> clazz) throws IOException{
        return get(url,null,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, clazz);
    }


    default <T> T post(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset, Class<T> clazz) throws IOException{
        String result = post(url, body, contentType, headers, connectTimeout, readTimeout, bodyCharset, resultCharset);
        return convert(result , clazz);
    }

    default <T> T post(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, Class<T> clazz) throws IOException{
        return post(url,body,contentType,headers,connectTimeout,readTimeout, DEFAULT_CHARSET,DEFAULT_CHARSET , clazz);
    }
    default <T> T post(String url, String body, String contentType, Map<String, String> headers, String bodyCharset, String resultCharset, Class<T> clazz) throws IOException{
        return post(url,body,contentType,headers,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT,bodyCharset,resultCharset , clazz);
    }
    default <T> T  post(String url, String body, String contentType, Map<String, String> headers, Class<T> clazz) throws IOException{
        return post(url,body,contentType,headers,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, clazz);
    }
    default <T> T  post(String url, String body, String contentType, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset, Class<T> clazz) throws IOException{
        return post(url,body,contentType,null,connectTimeout,readTimeout,bodyCharset,resultCharset , clazz);
    }
    default <T> T  post(String url, String body, String contentType, int connectTimeout, int readTimeout, Class<T> clazz) throws IOException{
        return post(url,body,contentType,null,connectTimeout,readTimeout , clazz);
    }
    /**
     * @see HttpClient#post(String, String, String, Map, int, int, String, String)
     */
    default <T> T  post(String url, String body, String contentType, String bodyCharset, String resultCharset, Class<T> clazz) throws IOException{
        return post(url,body,contentType,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT,bodyCharset,resultCharset , clazz);
    }
    default <T> T  post(String url, String body, String contentType, Class<T> clazz) throws IOException{
        return post(url,body,contentType,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, clazz);
    }
    default <T> T  postJson(String url, String body, String bodyCharset, String resultCharset, Class<T> clazz) throws IOException{
        return post(url,body,JSON_WITH_DEFAULT_CHARSET,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT,bodyCharset,resultCharset , clazz);
    }
    default <T> T  postJson(String url, String body, Class<T> clazz) throws IOException{
        return post(url,body,JSON_WITH_DEFAULT_CHARSET,null,DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, clazz);
    }

    /**参数用 =和& 连接*/
    default <T> T  post(String url, Map<String, String> params, Map<String, String> headers, String bodyCharset, String resultCharset, Class<T> clazz) throws IOException{
        return post(url, ParamUtil.contactMap(params , bodyCharset),FORM_URLENCODED_WITH_DEFAULT_CHARSET,headers,bodyCharset,resultCharset , clazz);
    }
    default <T> T  post(String url, Map<String, String> params, Map<String, String> headers, Class<T> clazz) throws IOException{
        return post(url, ParamUtil.contactMap(params , DEFAULT_CHARSET),FORM_URLENCODED_WITH_DEFAULT_CHARSET,headers , clazz);
    }
    default <T> T  post(String url, Map<String, String> params, String bodyCharset, String resultCharset, Class<T> clazz) throws IOException{
        return post(url, ParamUtil.contactMap(params , bodyCharset),FORM_URLENCODED_WITH_DEFAULT_CHARSET,null,bodyCharset,resultCharset , clazz);
    }
    default <T> T  post(String url, Map<String, String> params, Class<T> clazz) throws IOException{
        return post(url, ParamUtil.contactMap(params , DEFAULT_CHARSET),FORM_URLENCODED_WITH_DEFAULT_CHARSET,null , clazz);
    }

    default <T> T  get(Request request, Class<T> clazz) throws IOException{
        Response response = get(request);
        return convert(response.getBody() , clazz);
    }
    default <T> T  post(Request request, Class<T> clazz) throws IOException{
        Response response = post(request);
        return convert(response.getBody() , clazz);
    }
}
