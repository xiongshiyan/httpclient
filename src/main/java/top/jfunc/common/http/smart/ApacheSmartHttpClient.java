package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.basic.ApacheHttpClient;
import top.jfunc.common.utils.IoUtil;
import org.apache.http.HttpEntityEnclosingRequest;

import java.io.File;
import java.io.IOException;

/**
 * 使用Apache SmartHttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheSmartHttpClient extends ApacheHttpClient implements SmartHttpClient {
    @Override
    public Response get(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(ParamUtil.contactUrlParams(request.getUrl(), request.getParams() , request.getBodyCharset()), Method.GET,
                request.getContentType(), null, request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(),
                request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request.setUrl(ParamUtil.contactUrlParams(request.getUrl(), request.getParams(), getBodyCharsetWithDefault(request.getBodyCharset()))) ,
                Method.GET , null , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response post(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(),
                r -> setRequestBody(r, request.getBody(), request.getBodyCharset()), request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request, Method.POST ,
                r -> setRequestBody(r, request.getBody(), getBodyCharsetWithDefault(request.getBodyCharset())) , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response httpMethod(Request req, Method method) throws IOException {
        Request request = beforeTemplate(req);
        ContentCallback<HttpEntityEnclosingRequest> contentCallback = null;
        if(method.hasContent()){
            contentCallback = r -> setRequestBody(r, request.getBody(), getBodyCharsetWithDefault(request.getBodyCharset()));
        }
        Response response = template(request, method , contentCallback, Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public byte[] getAsBytes(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(), Method.GET, request.getContentType(),null, request.getHeaders(),
                request.getConnectionTimeout(),request.getReadTimeout() , request.getResultCharset(),request.isIncludeHeaders(),
                (s,b,r,h)-> IoUtil.stream2Bytes(b));*/
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(), Method.GET, request.getContentType(),null, request.getHeaders() ,
                request.getConnectionTimeout(),request.getReadTimeout() , request.getResultCharset(),request.isIncludeHeaders(),
                (s,b,r,h)-> IoUtil.copy2File(b, request.getFile()));*/
        return template(request , Method.GET, null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(), r -> addFormFiles(r, request.getFormFiles()),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        /*使用更全的 ，支持文件和参数一起上传 */

        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(), r -> addFormFiles(r, request.getParams() , request.getFormFiles()),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request , Method.POST ,
                r -> addFormFiles(r, request.getParams(), request.getFormFiles()) , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response afterTemplate(Request request, Response response) throws IOException{
        if(request.isRedirectable() && response.needRredirect()){
            return get(Request.of(response.getRedirectUrl()));
        }
        return response;
    }
}
