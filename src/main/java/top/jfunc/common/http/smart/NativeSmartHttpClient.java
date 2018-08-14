package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.basic.NativeHttpClient;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.IoUtil;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeSmartHttpClient extends NativeHttpClient implements SmartHttpClient {
    @Override
    public Response get(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(ParamUtil.contactUrlParams(request.getUrl(), request.getParams() , request.getBodyCharset()), Method.GET,
                request.getContentType(), null, request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(),
                request.getResultCharset(), request.isIncludeHeaders(), Response::with);*/
        Response response = template(request.setUrl(ParamUtil.contactUrlParams(request.getUrl(), request.getParams(), request.getBodyCharset())) ,
                Method.GET , null , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response post(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(),
                connection -> writeContent(connection, request.getBody(), request.getBodyCharset()),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(),
                request.getResultCharset(), request.isIncludeHeaders(), Response::with);*/
        Response response = template(request, Method.POST , connection -> writeContent(connection, request.getBody(), request.getBodyCharset()) , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response httpMethod(Request req, Method method) throws IOException {
        Request request = beforeTemplate(req);
        ContentCallback<HttpURLConnection> contentCallback = null;
        if(method.hasContent()){
            contentCallback = connection -> writeContent(connection, request.getBody(), request.getBodyCharset());
        }
        Response response = template(request, method , contentCallback , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public byte[] getAsBytes(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(), Method.GET, request.getContentType(), null,request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders() ,
                (s,b,r,h)-> IoUtil.stream2Bytes(b));*/
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(), Method.GET, request.getContentType(), null,request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders() ,
                (s,b,r,h)-> IoUtil.copy2File(b, request.getFile()));*/
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*ArrayListMultimap<String, String> headers = mergeHeaders(request.getHeaders(), request.getFormFiles());
        Response response = template(request.getUrl(), Method.POST, null, connect -> this.upload0(connect, request.getFormFiles()),
                headers, request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        /*使用更全的 ，支持文件和参数一起上传 */

        ArrayListMultimap<String, String> headers = mergeHeaders(request.getHeaders());
        /*Response response = template(request.getUrl(), Method.POST, null, connect -> this.upload0(connect, request.getParams() ,request.getFormFiles()),
                headers, request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request.setHeaders(headers), Method.POST ,
                connect -> this.upload0(connect, request.getParams(), request.getFormFiles()) , Response::with);
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
