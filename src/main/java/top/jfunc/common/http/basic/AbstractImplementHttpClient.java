package top.jfunc.common.http.basic;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.smart.AbstractSmartHttpClient;
import top.jfunc.common.http.smart.SmartHttpClient;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * 这个类的地位在{@link HttpClient} 体系中的类比于在{@link SmartHttpClient}体系中的{@link AbstractSmartHttpClient}
 * 提供统一异常处理、header处理
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
}
