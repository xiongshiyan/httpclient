package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.Config;
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
public abstract class AbstractImplementSmartHttpClient<CC> extends AbstractSmartHttpClient<CC> implements TemplateInterceptor {
    /**
     * 统一的拦截和异常处理：最佳实践使用拦截器代替子类复写
     * @inheritDoc
     */
    @Override
    public <R> R template(HttpRequest httpRequest, ContentCallback<CC> contentCallback, ResultCallback<R> resultCallback) throws IOException {
        //在接口方法入口处调用了init(HttpRequest)方法将系统Config设置到HttpRequest了
        Config config = httpRequest.getConfig();

        //1.子类处理
        HttpRequest h = beforeTemplate(httpRequest);
        //2.拦截器在之前处理
        HttpRequest request = config.onBeforeIfNecessary(h);
        try {
            //3.真正的实现
            R response = doInternalTemplate(request , contentCallback , resultCallback);
            //4.拦截器过滤
            config.onBeforeReturnIfNecessary(request , response);
            //5.子类处理
            return afterTemplate(request , response);
        } catch (IOException e) {
            //6.1.拦截器在抛异常的时候处理
            config.onErrorIfNecessary(request , e);
            throw e;
        } catch (Exception e) {
            //6.2.拦截器在抛异常的时候处理
            config.onErrorIfNecessary(request, e);
            throw new RuntimeException(e);
        }finally {
            //7.拦截器在任何时候都处理
            config.onFinallyIfNecessary(httpRequest);
        }
    }

    /**
     * 子类实现真正的自己的
     * @param httpRequest HttpRequest
     * @param contentCallback 处理请求体的
     * @param resultCallback 结果处理器
     * @param <R> 处理的结果
     * @return 处理的结果
     * @throws Exception Exception
     */
    abstract protected  <R> R doInternalTemplate(HttpRequest httpRequest, ContentCallback<CC> contentCallback, ResultCallback<R> resultCallback) throws Exception;

    /**
     * 使用{@link HttpRequest}体系来实现{@link HttpTemplate}接口方法
     * @inheritDoc
     */
    @Override
    public  <R> R template(String url, Method method , String contentType, ContentCallback<CC> contentCallback, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset , boolean includeHeader , ResultCallback<R> resultCallback) throws IOException {
        HttpRequest httpRequest = CommonRequest.of(url);
        init(httpRequest , method);

        httpRequest.setContentType(contentType);
        if(null != headers){
            headers.forEachKeyValue((k,v)->httpRequest.addHeader(k , v));
        }

        httpRequest.setConnectionTimeout(connectTimeout);
        httpRequest.setReadTimeout(readTimeout);

        if(null != resultCharset){
            httpRequest.setResultCharset(resultCharset);
        }
        httpRequest.setIncludeHeaders(includeHeader);


        return template(httpRequest , contentCallback , resultCallback);
    }
}

