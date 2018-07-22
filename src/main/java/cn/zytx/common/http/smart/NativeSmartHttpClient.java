package cn.zytx.common.http.smart;

import cn.zytx.common.http.*;
import cn.zytx.common.http.basic.NativeHttpClient;
import cn.zytx.common.utils.ArrayListMultimap;
import cn.zytx.common.utils.IoUtil;

import java.io.*;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeSmartHttpClient extends NativeHttpClient implements SmartHttpClient {
    @Override
    public Response get(Request req) throws IOException {
        Request request = beforeTemplate(req);
        Response response = template(ParamUtil.contactUrlParams(request.getUrl(), request.getParams() , request.getBodyCharset()), Method.GET,
                request.getContentType(), null, request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(),
                request.getResultCharset(), request.isIncludeHeaders(), Response::with);
        return afterTemplate(response);
    }

    @Override
    public Response post(Request req) throws IOException {
        Request request = beforeTemplate(req);
        Response response = template(request.getUrl(), Method.POST, request.getContentType(),
                connection -> writeContent(connection, request.getBody(), request.getBodyCharset()),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(),
                request.getResultCharset(), request.isIncludeHeaders(), Response::with);
        return afterTemplate(response);
    }

    @Override
    public byte[] getAsBytes(Request req) throws IOException {
        Request request = beforeTemplate(req);
        return template(request.getUrl(), Method.GET, request.getContentType(), null,request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders() ,
                (b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(Request req) throws IOException {
        Request request = beforeTemplate(req);
        return template(request.getUrl(), Method.GET, request.getContentType(), null,request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders() ,
                (b,r,h)-> IoUtil.copy2File(b, request.getFile()));
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
        Response response = template(request.getUrl(), Method.POST, null, connect -> this.upload0(connect, request.getParams() ,request.getFormFiles()),
                headers, request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);
        return afterTemplate(response);
    }
}
