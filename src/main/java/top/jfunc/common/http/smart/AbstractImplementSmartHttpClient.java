package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.HttpTemplate;
import top.jfunc.common.http.basic.UnpackedParameterHttpClient;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.basic.CommonRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * 更具体的实现相关的公共部分
 * 实现者只需要实现HttpTemplate接口、处理POST Body、文件上传Body即可
 * @see HttpTemplate
 * @see SmartHttpTemplate
 * @see UnpackedParameterHttpClient
 * @see HttpRequestResultCallbackHttpClient
 * @see SmartHttpClient
 * @author xiongshiyan at 2019/5/8 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractImplementSmartHttpClient<CC> extends AbstractSmartHttpClient<CC> {
    /**
     * 统一的拦截和异常处理：最佳实践使用拦截器代替子类复写
     * @inheritDoc
     */
    @Override
    public <R> R template(HttpRequest httpRequest, Method method, ContentCallback<CC> contentCallback, ResultCallback<R> resultCallback) throws IOException {
        //1.子类处理
        HttpRequest h = beforeTemplate(httpRequest);
        //2.拦截器在之前处理
        HttpRequest request = onBeforeIfNecessary(h, method);
        try {
            //3.真正的实现
            R response = doInternalTemplate(request , method , contentCallback , resultCallback);
            //4.拦截器过滤
            onBeforeReturnIfNecessary(request , response);
            //5.子类处理
            return afterTemplate(request , response);
        } catch (IOException e) {
            //6.1.拦截器在抛异常的时候处理
            onErrorIfNecessary(request , e);
            throw e;
        } catch (Exception e) {
            //6.2.拦截器在抛异常的时候处理
            onErrorIfNecessary(request, e);
            throw new RuntimeException(e);
        }finally {
            //7.拦截器在任何时候都处理
            onFinallyIfNecessary(httpRequest);
        }
    }

    /**
     * 子类实现真正的自己的
     * @param httpRequest HttpRequest
     * @param method 请求方法
     * @param contentCallback 处理请求体的
     * @param resultCallback 结果处理器
     * @param <R> 处理的结果
     * @return 处理的结果
     * @throws Exception Exception
     */
    abstract protected  <R> R doInternalTemplate(HttpRequest httpRequest, Method method, ContentCallback<CC> contentCallback, ResultCallback<R> resultCallback) throws Exception;

    /**
     * 使用{@link HttpRequest}体系来实现{@link HttpTemplate}接口方法
     * @inheritDoc
     */
    @Override
    public  <R> R template(String url, Method method , String contentType, ContentCallback<CC> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , boolean includeHeader , ResultCallback<R> resultCallback) throws IOException {
        HttpRequest httpRequest = CommonRequest.of(url);
        httpRequest.setContentType(contentType);
        if(null != headers){
            headers.forEachKeyValue((k,v)->httpRequest.addHeader(k , v));
        }
        if(null != connectTimeout){
            httpRequest.setConnectionTimeout(connectTimeout);
        }
        if(null != readTimeout){
            httpRequest.setReadTimeout(readTimeout);
        }
        if(null != resultCharset){
            httpRequest.setResultCharset(resultCharset);
        }
        httpRequest.setIncludeHeaders(includeHeader);

        return template(httpRequest , method , contentCallback , resultCallback);
    }

    /**
     * 处理请求的headers
     * @param target 给谁设置，每个框架自行实现
     * @param httpRequest HttpRequest
     * @param completedUrl completedUrl
     * @throws IOException IOException
     */
    protected void configHeaders(Object target , HttpRequest httpRequest, String completedUrl) throws IOException {
        //1.合并默认headers
        MultiValueMap<String, String> requestHeaders = mergeDefaultHeaders(httpRequest.getHeaders());

        //2.处理cookie
        requestHeaders = addCookieIfNecessary(completedUrl, requestHeaders);

        //3.真正设置
        setRequestHeaders(target , httpRequest , requestHeaders);
    }

    /**
     * 每个框架自己实现自己的设置方法
     * @param target 给谁设置
     * @param httpRequest HttpRequest
     * @param handledHeaders 处理过后的headers
     * @throws IOException IOException
     */
    protected abstract void setRequestHeaders(Object target , HttpRequest httpRequest , MultiValueMap<String , String> handledHeaders) throws IOException;

    /**
     * 处理返回的header，并处理了cookie
     * @param source 每种实现都有自己的
     * @param httpRequest HttpRequest
     * @param completedUrl completedUrl
     * @return headers
     * @throws IOException IOException
     */
    protected MultiValueMap<String, String> determineHeaders(Object source, HttpRequest httpRequest, String completedUrl) throws IOException {
        ///boolean includeHeaders = httpRequest.isIncludeHeaders();
        ///1.如果要支持cookie，必须读取header
        if(supportCookie()){
            //includeHeaders = HttpRequest.INCLUDE_HEADERS;
            httpRequest.includeHeaders();
        }
        //2.从响应中获取headers
        MultiValueMap<String, String> responseHeaders = parseResponseHeaders(source, httpRequest);

        //3.处理cookie
        saveCookieIfNecessary(httpRequest, completedUrl , responseHeaders);

        return responseHeaders;
    }

    protected void saveCookieIfNecessary(HttpRequest httpRequest, String completedUrl , MultiValueMap<String, String> responseHeaders) throws IOException {
        saveCookieIfNecessary(completedUrl, responseHeaders);
    }

    /**
     * 每种实现真正实现自己的获取header的方法
     * @param source 源
     * @param httpRequest httpRequest
     * @return headers
     */
    protected abstract MultiValueMap<String, String> parseResponseHeaders(Object source, HttpRequest httpRequest);
}

