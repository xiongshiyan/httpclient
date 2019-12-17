package top.jfunc.common.http.basic;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.smart.AbstractImplementSmartHttpClient;
import top.jfunc.common.http.smart.SmartHttpClient;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * 这个类的地位在{@link HttpClient} 体系中的类比于在{@link SmartHttpClient}体系中的{@link AbstractImplementSmartHttpClient}
 * 提供统一异常处理、header处理
 * @see UnpackedParameterHttpClient
 * @see HttpTemplate
 * @author xiongshiyan at 2019/12/16 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractImplementHttpClient<CC> extends AbstractHttpClient<CC> {
    /**
     * 统一的异常处理
     * @inheritDoc
     */
    @Override
    public <R> R template(String url, Method method, String contentType, ContentCallback<CC> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, boolean includeHeaders, ResultCallback<R> resultCallback) throws IOException {
        try {
            return doInternalTemplate(url , method , contentType , contentCallback , headers , connectTimeout , readTimeout , resultCharset , includeHeaders , resultCallback);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 子类复写此方法,实现自己的请求逻辑即可
     * @param url URL
     * @param method 请求方法
     * @param contentType Content-Type
     * @param contentCallback 请求体处理器
     * @param headers 请求header
     * @param connectTimeout 链接超时时间
     * @param readTimeout 读超时时间
     * @param resultCharset 响应体字符编码
     * @param includeHeaders 是否包含header
     * @param resultCallback 结果转换器
     * @param <R> 返回值泛型
     * @return <R> R
     * @throws Exception Exception
     */
    protected abstract <R> R doInternalTemplate(String url, Method method, String contentType, ContentCallback<CC> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, boolean includeHeaders, ResultCallback<R> resultCallback) throws Exception;


    /**
     * 处理请求的headers
     * @param target 给谁设置，每个框架自行实现
     * @param completedUrl completedUrl
     * @param headers 请求的headers
     * @throws IOException IOException
     */
    protected void configHeaders(Object target , String completedUrl , String contentType , MultiValueMap<String , String> headers) throws IOException {
        //1.合并默认headers
        MultiValueMap<String, String> requestHeaders = mergeDefaultHeaders(headers);

        //2.处理cookie
        requestHeaders = addCookieIfNecessary(completedUrl, requestHeaders);

        //3.真正设置
        setRequestHeaders(target , contentType , requestHeaders);
    }

    /**
     * 每个框架自己实现自己的设置方法
     * @param target 给谁设置
     * @param contentType contentType
     * @param handledHeaders 处理过后的headers
     * @throws IOException IOException
     */
    protected abstract void setRequestHeaders(Object target, String contentType , MultiValueMap<String , String> handledHeaders) throws IOException;

    /**
     * 处理返回的header，并处理了cookie
     * @param source 每种实现都有自己的
     * @param completedUrl completedUrl
     * @return headers
     * @throws IOException IOException
     */
    protected MultiValueMap<String, String> determineHeaders(Object source, String completedUrl , boolean includeHeaders) throws IOException {
        boolean s = includeHeaders;
        ///1.如果要支持cookie，必须读取header
        if(supportCookie()){
            //includeHeaders = HttpRequest.INCLUDE_HEADERS;
            s = true;
        }
        //2.从响应中获取headers
        MultiValueMap<String, String> responseHeaders = parseResponseHeaders(source , s);

        //3.处理cookie
        saveCookieIfNecessary(completedUrl , responseHeaders);

        return responseHeaders;
    }

    /**
     * 每种实现真正实现自己的获取header的方法
     * @param source 源
     * @param includeHeaders 是否获取headers
     * @return headers
     */
    protected abstract MultiValueMap<String, String> parseResponseHeaders(Object source , boolean includeHeaders);

}
