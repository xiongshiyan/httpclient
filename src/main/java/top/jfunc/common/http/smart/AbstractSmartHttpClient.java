package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.AbstractHttpClient;
import top.jfunc.common.http.request.FormRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * 提供了{@link SmartHttpClient}接口的实现
 * @see SmartHttpClient
 * @see AbstractSmartHttpClient#bodyContentCallback(Method, String, String, String)
 * @see AbstractSmartHttpClient#uploadContentCallback(MultiValueMap, String, Iterable)
 * @author xiongshiyan at 2019/5/8 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractSmartHttpClient<CC> extends AbstractHttpClient<CC> implements SmartHttpClient, SmartHttpTemplate<CC> {

    @Override
    public <R> R get(HttpRequest httpRequest , ResultCallback<R> resultCallback) throws IOException {
        return template(httpRequest, Method.GET , null , resultCallback);
    }

    @Override
    public <R> R post(StringBodyRequest stringBodyRequest , ResultCallback<R> resultCallback) throws IOException {
        String bodyCharset = calculateBodyCharset(stringBodyRequest.getBodyCharset(), stringBodyRequest.getContentType());
        stringBodyRequest.setBodyCharset(bodyCharset);
        String body = stringBodyRequest.getBody();
        return template(stringBodyRequest, Method.POST ,
                bodyContentCallback(Method.POST , body, bodyCharset, stringBodyRequest.getContentType()) ,
                resultCallback);
    }

    /**
     * post就支持form表单
     * @see StringBodyRequest
     * @see FormRequest
     */
    @Override
    public <R> R form(FormRequest formRequest, ResultCallback<R> resultCallback) throws IOException{
        return post(formRequest , resultCallback);
    }

    @Override
    public <R> R upload(UploadRequest uploadRequest , ResultCallback<R> resultCallback) throws IOException {
        return template(uploadRequest , Method.POST ,
                uploadContentCallback(uploadRequest.getFormParams(),
                        calculateBodyCharset(uploadRequest.getParamCharset() , uploadRequest.getContentType()),
                        uploadRequest.getFormFiles()) , resultCallback);
    }

    /*------------------------HTTP方法通用支持---------------------------*/

    @Override
    public <R> R http(HttpRequest httpRequest, Method method, ResultCallback<R> resultCallback) throws IOException {
        ContentCallback<CC> contentCallback = null;
        if(method.hasContent() && httpRequest instanceof StringBodyRequest){
            StringBodyRequest stringBodyRequest = (StringBodyRequest) httpRequest;
            String bodyCharset = calculateBodyCharset(stringBodyRequest.getBodyCharset() , stringBodyRequest.getContentType());
            stringBodyRequest.setBodyCharset(bodyCharset);
            String body = stringBodyRequest.getBody();
            contentCallback = bodyContentCallback(method ,body, bodyCharset, httpRequest.getContentType());
        }
        return template(httpRequest, method , contentCallback, resultCallback);
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
    public <R> R put(StringBodyRequest stringBodyRequest, ResultCallback<R> resultCallback) throws IOException {
        String bodyCharset = calculateBodyCharset(stringBodyRequest.getBodyCharset(), stringBodyRequest.getContentType());
        stringBodyRequest.setBodyCharset(bodyCharset);
        String body = stringBodyRequest.getBody();
        return template(stringBodyRequest, Method.PUT ,
                bodyContentCallback(Method.PUT , body, bodyCharset, stringBodyRequest.getContentType()) ,
                resultCallback);
    }



    /*------------------------PATCH---------------------------*/

    @Override
    public <R> R patch(StringBodyRequest stringBodyRequest, ResultCallback<R> resultCallback) throws IOException {
        String bodyCharset = calculateBodyCharset(stringBodyRequest.getBodyCharset(), stringBodyRequest.getContentType());
        stringBodyRequest.setBodyCharset(bodyCharset);
        String body = stringBodyRequest.getBody();
        return template(stringBodyRequest, Method.PATCH ,
                bodyContentCallback(Method.PATCH , body, bodyCharset, stringBodyRequest.getContentType()) ,
                resultCallback);
    }



    /*------------------------DELETE---------------------------*/

    @Override
    public <R> R delete(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        return template(httpRequest , Method.DELETE , null , resultCallback);
    }



    /*------------------------TRACE---------------------------*/

    @Override
    public <R> R trace(HttpRequest httpRequest, ResultCallback<R> resultCallback) throws IOException {
        //必须要响应头
        httpRequest.setIncludeHeaders(HttpRequest.INCLUDE_HEADERS);
        //设置忽略响应体
        httpRequest.setIgnoreResponseBody(HttpRequest.IGNORE_RESPONSE_BODY);

        /// trace没有body
        /*
        ContentCallback<CC> contentCallback = null;
        if(httpRequest instanceof StringBodyRequest){
            StringBodyRequest stringBodyRequest = (StringBodyRequest) httpRequest;
            String bodyCharset = calculateBodyCharset(stringBodyRequest.getBodyCharset() , stringBodyRequest.getContentType());
            stringBodyRequest.setBodyCharset(bodyCharset);
            String body = stringBodyRequest.getBody();
            contentCallback = bodyContentCallback(Method.TRACE ,body, bodyCharset, httpRequest.getContentType());
        }*/
        return template(httpRequest, Method.TRACE , null, resultCallback);
    }


    /**
     * 处理请求体的回调
     * @param method 请求方法
     * @param body 请求体
     * @param bodyCharset 编码
     * @param contentType Content-Type
     * @return ContentCallback<CC>
     * @throws IOException IOException
     */
    @Override
    abstract protected ContentCallback<CC> bodyContentCallback(Method method , String body , String bodyCharset , String contentType) throws IOException;

    /**
     * 处理文件上传
     * @param params 参数，k-v，可能为null
     * @param paramCharset 参数编码
     * @param formFiles 文件信息
     * @return ContentCallback<CC>
     * @throws IOException IOException
     */
    @Override
    protected abstract ContentCallback<CC> uploadContentCallback(MultiValueMap<String, String> params , String paramCharset , Iterable<FormFile> formFiles) throws IOException;
}

