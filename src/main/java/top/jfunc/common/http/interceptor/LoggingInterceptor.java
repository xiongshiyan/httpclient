package top.jfunc.common.http.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.common.http.request.*;
import top.jfunc.common.http.smart.Response;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import static top.jfunc.common.utils.StrUtil.CRLF;

/**
 * 打印日志的拦截器
 * @author xiongshiyan at 2019/7/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class LoggingInterceptor extends InterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    /**默认超过1M就不打印了*/
    private static final int DEFAULT_THRESHOLD = 1024*1024;
    private int threshold = DEFAULT_THRESHOLD;

    public LoggingInterceptor(int threshold) {
        this.threshold = threshold;
    }

    public LoggingInterceptor() {
    }

    @Override
    public HttpRequest onBefore(HttpRequest httpRequest) {
        try {
            logger.info("请求方法:"+httpRequest.getMethod().name());
            logger.info("httpRequestClass:"+httpRequest.getClass());
            StringBuilder builder = new StringBuilder("url:"+httpRequest.getUrl()+CRLF);
            if(notEmpty(httpRequest.getRouteParams())){
                builder.append("routeParam:"+httpRequest.getRouteParams()+CRLF);
            }
            if(notEmpty(httpRequest.getQueryParams())){
                builder.append("queryParam:"+httpRequest.getQueryParams()+CRLF);
            }
            if(notEmpty(httpRequest.getQueryParamCharset())){
                builder.append("queryCharset:"+httpRequest.getQueryParamCharset()+CRLF);
            }
            if(notEmpty(httpRequest.getContentType())){
                builder.append("contentType:"+httpRequest.getContentType()+CRLF);
            }
            if(notEmpty(httpRequest.getHeaders())){
                builder.append("headers:"+httpRequest.getHeaders()+CRLF);
            }
            if(notEmpty(httpRequest.getConnectionTimeout())){
                builder.append("connectionTimeout:"+httpRequest.getConnectionTimeout()+CRLF);
            }
            if(notEmpty(httpRequest.getReadTimeout())){
                builder.append("readTimeout:"+httpRequest.getReadTimeout()+CRLF);
            }
            builder.append("resultCharset:"+httpRequest.getResultCharset()+CRLF);
            builder.append("retainResponseHeaders:"+httpRequest.retainResponseHeaders()+CRLF);
            builder.append("ignoreResponseBody:"+httpRequest.ignoreResponseBody()+CRLF);
            builder.append("followRedirects:"+httpRequest.followRedirects()+CRLF);

            if(notEmpty(httpRequest.getAttributes())){
                builder.append("attributes:"+httpRequest.getAttributes()+CRLF);
            }
            if(httpRequest instanceof StringBodyRequest && !(httpRequest instanceof FormRequest)){
                StringBodyRequest stringBodyRequest = (StringBodyRequest) httpRequest;
                String body = stringBodyRequest.getBody();
                String toPrint = body.length() <= threshold ? body : body.substring(0 , threshold);
                builder.append("body:"+ toPrint +CRLF
                        +"bodyCharset:"+stringBodyRequest.getBodyCharset()+CRLF);
            }
            if(httpRequest instanceof FormRequest){
                FormRequest formRequest = (FormRequest) httpRequest;
                builder.append("form:"+formRequest.getFormParams()+CRLF
                        +"formCharset:"+formRequest.getBodyCharset()+CRLF);
            }
            if(httpRequest instanceof UploadRequest){
                UploadRequest uploadRequest = (UploadRequest) httpRequest;
                builder.append("form:"+uploadRequest.getFormParams()+CRLF
                        +"formCharset:"+uploadRequest.getParamCharset()+CRLF
                        +"formFiles:"+ uploadRequest.getFormFiles()+CRLF);
            }
            if(httpRequest instanceof DownloadRequest){
                DownloadRequest downloadRequest = (DownloadRequest) httpRequest;
                builder.append("fileToSave:"+downloadRequest.getFile().getAbsolutePath()+CRLF);
            }

            logger.info(builder.toString());
        } catch (Exception e) {
            logger.error(e.getMessage() , e);
        }
        return super.onBefore(httpRequest);
    }

    protected boolean notEmpty(Object o){
        if (null == o) {
            return false;
        }
        if(o instanceof Map){
            return ((Map) o).size() > 0;
        }
        if(o instanceof Collection){
            return ((Collection) o).size() > 0;
        }
        if(o.getClass().isArray()){
            return Array.getLength(o) > 0;
        }
        if(o instanceof CharSequence){
            return ((CharSequence) o).length() > 0;
        }
        return true;
    }

    @Override
    public void onBeforeReturn(HttpRequest httpRequest, Object returnValue) {
        if(returnValue instanceof Response){
            Response response = (Response) returnValue;
            logger.info("返回响应码:" + response.getStatusCode());
            logger.info("响应头:" + response.getHeaders());
        }
        logger.info("返回值:" + returnValue);
        super.onBeforeReturn(httpRequest, returnValue);
    }

    @Override
    public void onError(HttpRequest httpRequest, Exception exception) {
        logger.error(exception.getMessage() , exception);
    }
}
