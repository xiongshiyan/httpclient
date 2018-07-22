package cn.zytx.common.http.smart;

import cn.zytx.common.http.*;
import cn.zytx.common.http.basic.ApacheHttpClient;
import cn.zytx.common.utils.IoUtil;

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
        Response response = template(ParamUtil.contactUrlParams(request.getUrl(), request.getParams() , request.getBodyCharset()), Method.GET,
                request.getContentType(), null, request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(),
                request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);
        return afterTemplate(response);
    }

    @Override
    public Response post(Request req) throws IOException {
        Request request = beforeTemplate(req);
        Response response = template(request.getUrl(), Method.POST, request.getContentType(),
                r -> setRequestBody(r, request.getBody(), request.getBodyCharset()), request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);
        return afterTemplate(response);
    }

    @Override
    public byte[] getAsBytes(Request req) throws IOException {
        Request request = beforeTemplate(req);
        return template(request.getUrl(), Method.GET, request.getContentType(),null, request.getHeaders(),
                request.getConnectionTimeout(),request.getReadTimeout() , request.getResultCharset(),request.isIncludeHeaders(),
                (b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(Request req) throws IOException {
        Request request = beforeTemplate(req);
        return template(request.getUrl(), Method.GET, request.getContentType(),null, request.getHeaders() ,
                request.getConnectionTimeout(),request.getReadTimeout() , request.getResultCharset(),request.isIncludeHeaders(),
                (b,r,h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(), r -> addFormFiles(r, request.getFormFiles()),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        /*使用更全的 ，支持文件和参数一起上传 */

        Response response = template(request.getUrl(), Method.POST, request.getContentType(), r -> addFormFiles(r, request.getParams() , request.getFormFiles()),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);
        return afterTemplate(response);
    }
}
