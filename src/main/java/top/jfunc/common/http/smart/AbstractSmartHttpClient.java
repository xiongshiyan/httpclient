package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.AbstractHttpClient;
import top.jfunc.common.http.basic.HttpClient;
import top.jfunc.common.http.basic.HttpTemplate;
import top.jfunc.common.http.request.FormRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.basic.CommonRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * 实现者只需要实现HttpTemplate接口、处理POST Body、文件上传Body即可
 * @see SmartHttpClient
 * @see AbstractSmartHttpClient#bodyContentCallback(Method, String, String, String)
 * @see AbstractSmartHttpClient#uploadContentCallback(MultiValueMap, String, Iterable)
 * @author xiongshiyan at 2019/5/8 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractSmartHttpClient<CC> extends AbstractHttpClient<CC> implements SmartHttpClient, SmartHttpTemplate<CC>, TemplateInterceptor {
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
     * 使用HttpRequest体系来实现HttpTemplate接口方法
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
     * {@link HttpTemplate}和{@link HttpClient}接口体系实现此方法，
     * 而{@link top.jfunc.common.http.smart.SmartHttpTemplate}和{@link top.jfunc.common.http.smart.SmartHttpClient}接口体系不需要实现之
     * 而是直接复写{@link HttpTemplate#template(String, Method, String, ContentCallback, MultiValueMap, Integer, Integer, String, boolean, ResultCallback)}
     * 所以抛出异常
     */
    @Override
    protected <R> R doInternalTemplate(String url, Method method, String contentType, ContentCallback<CC> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, boolean includeHeaders, ResultCallback<R> resultCallback) throws Exception{
        throw new UnsupportedOperationException("HttpRequest实现体系不支持此种方式");
    }


    @Override
    public <R> R get(HttpRequest request , ResultCallback<R> resultCallback) throws IOException {
        return template(request, Method.GET , null , resultCallback);
    }

    @Override
    public <R> R post(StringBodyRequest request , ResultCallback<R> resultCallback) throws IOException {
        String body = request.getBody();
        String bodyCharset = calculateBodyCharset(request.getBodyCharset(), request.getContentType());
        return template(request, Method.POST ,
                bodyContentCallback(Method.POST , body, bodyCharset, request.getContentType()) ,
                resultCallback);
    }

    /**
     * post就支持form表单
     * @see StringBodyRequest
     * @see FormRequest
     */
    @Override
    public <R> R form(FormRequest request, ResultCallback<R> resultCallback) throws IOException{
        return post(request , resultCallback);
    }

    @Override
    public <R> R upload(UploadRequest request , ResultCallback<R> resultCallback) throws IOException {
        return template(request , Method.POST ,
                uploadContentCallback(request.getFormParams(),
                        calculateBodyCharset(request.getParamCharset() , request.getContentType()),
                        request.getFormFiles()) , resultCallback);
    }

    /*------------------------HTTP方法通用支持---------------------------*/

    @Override
    public <R> R http(HttpRequest request, Method method, ResultCallback<R> resultCallback) throws IOException {
        ContentCallback<CC> contentCallback = null;
        if(method.hasContent() && request instanceof StringBodyRequest){
            StringBodyRequest bodyRequest = (StringBodyRequest) request;
            String body = bodyRequest.getBody();
            String bodyCharset = calculateBodyCharset(bodyRequest.getBodyCharset() , bodyRequest.getContentType());
            contentCallback = bodyContentCallback(method ,body, bodyCharset, request.getContentType());
        }
        return template(request, method , contentCallback, resultCallback);
    }



    /*------------------------HEAD---------------------------*/

    @Override
    public <R> R head(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        //必须要响应头
        httpRequest.setIncludeHeaders(HttpRequest.INCLUDE_HEADERS);
        //设置忽略响应体
        httpRequest.setIgnoreResponseBody(HttpRequest.IGNORE_RESPONSE_BODY);
        return template(httpRequest , Method.HEAD , null , resultCallback);
    }



    /*------------------------OPTIONS---------------------------*/

    @Override
    public <R> R options(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        //必须要响应头
        httpRequest.setIncludeHeaders(HttpRequest.INCLUDE_HEADERS);
        //设置忽略响应体
        httpRequest.setIgnoreResponseBody(HttpRequest.IGNORE_RESPONSE_BODY);
        return template(httpRequest , Method.OPTIONS , null , resultCallback);
    }



    /*------------------------PUT---------------------------*/

    @Override
    public <R> R put(StringBodyRequest request, ResultCallback<R> resultCallback) throws IOException {
        String body = request.getBody();
        String bodyCharset = calculateBodyCharset(request.getBodyCharset(), request.getContentType());
        return template(request, Method.PUT ,
                bodyContentCallback(Method.PUT , body, bodyCharset, request.getContentType()) ,
                resultCallback);
    }



    /*------------------------PATCH---------------------------*/

    @Override
    public <R> R patch(StringBodyRequest request, ResultCallback<R> resultCallback) throws IOException {
        String body = request.getBody();
        String bodyCharset = calculateBodyCharset(request.getBodyCharset(), request.getContentType());
        return template(request, Method.PATCH ,
                bodyContentCallback(Method.PATCH , body, bodyCharset, request.getContentType()) ,
                resultCallback);
    }



    /*------------------------DELETE---------------------------*/

    @Override
    public <R> R delete(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        return template(httpRequest , Method.DELETE , null , resultCallback);
    }



    /*------------------------TRACE---------------------------*/

    @Override
    public <R> R trace(HttpRequest request, ResultCallback<R> resultCallback) throws IOException {
        //必须要响应头
        request.setIncludeHeaders(HttpRequest.INCLUDE_HEADERS);
        //设置忽略响应体
        request.setIgnoreResponseBody(HttpRequest.IGNORE_RESPONSE_BODY);

        ContentCallback<CC> contentCallback = null;
        if(request instanceof StringBodyRequest){
            StringBodyRequest bodyRequest = (StringBodyRequest) request;
            String body = bodyRequest.getBody();
            String bodyCharset = calculateBodyCharset(bodyRequest.getBodyCharset() , bodyRequest.getContentType());
            contentCallback = bodyContentCallback(Method.TRACE ,body, bodyCharset, request.getContentType());
        }
        return template(request, Method.TRACE , contentCallback, resultCallback);
    }
}

