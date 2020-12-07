package top.jfunc.common.http.template;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.BodyContentCallbackCreator;
import top.jfunc.common.http.component.ContentCallbackCreator;
import top.jfunc.common.http.component.UploadContentCallbackCreator;
import top.jfunc.common.http.request.FormRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.response.ClientHttpResponse;
import top.jfunc.common.http.response.ClientHttpResponseConverter;

import java.io.IOException;

/**
 * @author xiongshiyan at 2019/4/2 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface SmartHttpTemplate<CC> {
    /**
     * 封装http请求的一般步骤
     * @param httpRequest 请求参数 {@link HttpRequest}代表一个http请求所有的参数，
     *                    可能是其子类{@link StringBodyRequest}、{@link UploadRequest}、{@link FormRequest}等
     * @param contentCallback 封装对BODY的处理，例如JSON或者文件上传。maybe null if doesn't have body
     * @see ContentCallbackCreator
     * @see BodyContentCallbackCreator
     * @see UploadContentCallbackCreator
     * @param clientHttpResponseConverter 对响应{@link ClientHttpResponse}进行进一步处理得到自己想要的
     * @throws IOException IOException
     * @return <R> 转换过后的结果
     */
    <R> R template(HttpRequest httpRequest,
                   ContentCallback<CC> contentCallback,
                   ClientHttpResponseConverter<R> clientHttpResponseConverter) throws IOException;
}
