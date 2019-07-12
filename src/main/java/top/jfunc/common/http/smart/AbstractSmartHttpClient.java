package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.AbstractHttpClient;
import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.basic.GetRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;

/**
 * 实现者只需要实现HttpTemplate接口、处理POST Body、文件上传Body即可
 * @see SmartHttpClient
 * @see AbstractSmartHttpClient#bodyContentCallback(String, String, String)
 * @see AbstractSmartHttpClient#uploadContentCallback(MultiValueMap, String, FormFile[])
 * @author xiongshiyan at 2019/5/8 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractSmartHttpClient<CC> extends AbstractHttpClient<CC> implements SmartHttpClient, SmartInterceptorHttpTemplate<CC> {

    /**
     * 使用HttpRequest体系来实现HttpTemplate接口方法
     * @param url URL
     * @param method 请求方法
     * @param contentType 请求体MIME类型
     * @param contentCallback 处理请求体的
     * @param headers headers
     * @param connectTimeout 连接超时时间
     * @param readTimeout 读取超时时间
     * @param resultCharset 结果字符集
     * @param includeHeader 是否结果包含header
     * @param resultCallback 结果处理器
     * @param <R> 结果
     * @return ResultCallback接口返回的
     * @throws IOException IOException
     */
    @Override
    public  <R> R template(String url, Method method , String contentType, ContentCallback<CC> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , boolean includeHeader , ResultCallback<R> resultCallback) throws IOException {
        HttpRequest httpRequest = GetRequest.of(url);
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

    @Override
    public Response get(HttpRequest request) throws IOException {
        return template(request, Method.GET , null , Response::with);
    }

    @Override
    public Response post(StringBodyRequest request) throws IOException {
        String body = request.getBody();
        String bodyCharset = calculateBodyCharset(request.getBodyCharset(), request.getContentType());
        return template(request, Method.POST ,
                bodyContentCallback(body, bodyCharset, request.getContentType()) ,
                Response::with);
    }

    @Override
    public <R> R http(HttpRequest request, Method method, ResultCallback<R> resultCallback) throws IOException {
        ContentCallback<CC> contentCallback = null;
        if(method.hasContent() && request instanceof StringBodyRequest){
            StringBodyRequest bodyRequest = (StringBodyRequest) request;
            String body = bodyRequest.getBody();
            String bodyCharset = calculateBodyCharset(bodyRequest.getBodyCharset() , bodyRequest.getContentType());
            contentCallback = bodyContentCallback(body, bodyCharset, request.getContentType());
        }
        return template(request, method , contentCallback, resultCallback);
    }

    @Override
    public byte[] getAsBytes(HttpRequest request) throws IOException {
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File download(DownloadRequest request) throws IOException {
        return template(request , Method.GET, null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(UploadRequest request) throws IOException {
        return template(request , Method.POST ,
                uploadContentCallback(request.getFormParams(),
                        calculateBodyCharset(request.getParamCharset() , request.getContentType()),
                        request.getFormFiles()) , Response::with);
    }
}

