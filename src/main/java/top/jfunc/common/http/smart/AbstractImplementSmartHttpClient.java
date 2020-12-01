package top.jfunc.common.http.smart;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.BodyContentCallbackCreator;
import top.jfunc.common.http.component.UploadContentCallbackCreator;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.response.ResponseConverter;
import top.jfunc.common.utils.IoUtil;

import java.io.IOException;

/**
 * 统一异常处理
 * @see SmartHttpTemplate
 * @see SimpleHttpClient
 * @see HttpRequestHttpClient
 * @see SmartHttpClient
 * @author xiongshiyan at 2019/5/8 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractImplementSmartHttpClient<CC> extends AbstractSmartHttpClient<CC> implements TemplateInterceptor {

    protected AbstractImplementSmartHttpClient(BodyContentCallbackCreator<CC> bodyContentCallbackCreator, UploadContentCallbackCreator<CC> uploadContentCallbackCreator) {
        super(bodyContentCallbackCreator, uploadContentCallbackCreator);
    }

    /**
     * 统一的拦截和异常处理：最佳实践使用拦截器代替子类复写
     * @inheritDoc
     */
    @Override
    public <R> R template(HttpRequest httpRequest, ContentCallback<CC> contentCallback, ResponseConverter<R> responseConverter) throws IOException {
        //在接口方法入口处调用了init(HttpRequest)方法将系统Config设置到HttpRequest了
        Config config = httpRequest.getConfig();

        //1.子类处理
        HttpRequest h = beforeTemplate(httpRequest);
        //2.拦截器在之前处理
        HttpRequest request = config.onBeforeIfNecessary(h);
        ClientHttpResponse clientHttpResponse = null;
        try {
            //3.真正的实现
            clientHttpResponse = doInternalTemplate(request , contentCallback);
            //4.拦截器过滤
            clientHttpResponse = config.onBeforeReturnIfNecessary(request , clientHttpResponse);
            //5.子类处理
            clientHttpResponse = afterTemplate(request, clientHttpResponse);
            //6.结果传唤
            return responseConverter.convert(clientHttpResponse, calculateResultCharset(h));
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
            //这一步特别关键，保证资源关闭
            IoUtil.close(clientHttpResponse);
        }
    }

    /**
     * 子类实现真正的自己的
     * @param httpRequest HttpRequest
     * @param contentCallback 处理请求体的
     * @return 处理的结果
     * @throws Exception Exception
     */
    abstract protected ClientHttpResponse doInternalTemplate(HttpRequest httpRequest, ContentCallback<CC> contentCallback) throws Exception;
}

