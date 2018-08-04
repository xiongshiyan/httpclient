package cn.zytx.common.http.smart;

import cn.zytx.common.http.Method;
import cn.zytx.common.http.ParamUtil;
import cn.zytx.common.http.base.ContentCallback;
import cn.zytx.common.http.basic.OkHttp3Client;
import cn.zytx.common.utils.IoUtil;
import okhttp3.MultipartBody;

import java.io.File;
import java.io.IOException;

/**
 * @author xiongshiyan at 2018/1/11
 */
public class OkHttp3SmartHttpClient extends OkHttp3Client implements SmartHttpClient {
    @Override
    public Response get(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(ParamUtil.contactUrlParams(request.getUrl(), request.getParams() , request.getBodyCharset()), Method.GET, request.getContentType(), null, request.getHeaders(),
                request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);
        return afterTemplate(request , response);*/
        Response response = template(request.setUrl(ParamUtil.contactUrlParams(request.getUrl(), request.getParams(), request.getBodyCharset())) , Method.GET , null , Response::with);
        return afterTemplate(request , response);
    }
    /**
     * @param req 请求体的编码，不支持，需要在contentType中指定
     */
    @Override
    public Response post(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(), d -> setRequestBody(d, Method.POST, stringBody(request.getBody(), request.getContentType())),
                request.getHeaders(), request.getConnectionTimeout(), request.getReadTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request, Method.POST , d -> setRequestBody(d, Method.POST, stringBody(request.getBody(), request.getContentType())) , Response::with);
        return afterTemplate(request , response);
    }


    @Override
    public Response httpMethod(Request req, Method method) throws IOException {
        Request request = beforeTemplate(req);
        ContentCallback<okhttp3.Request.Builder> contentCallback = null;
        if(method.hasContent()){
            contentCallback = d -> setRequestBody(d, method, stringBody(request.getBody(), request.getContentType()));
        }
        Response response = template(request, method , contentCallback , Response::with);
        return afterTemplate(request , response);
    }


    @Override
    public byte[] getAsBytes(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(),Method.GET,request.getContentType() ,null, request.getHeaders() ,
                request.getConnectionTimeout(),request.getReadTimeout(),request.getResultCharset(),request.isIncludeHeaders(),
                (s,b,r,h)-> IoUtil.stream2Bytes(b));*/
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(Request req) throws IOException {
        Request request = beforeTemplate(req);
        /*return template(request.getUrl(),Method.GET,request.getContentType(),null, request.getHeaders() ,
                request.getConnectionTimeout(),request.getConnectionTimeout(),request.getResultCharset(),request.isIncludeHeaders(),
                (s,b,r,h)-> IoUtil.copy2File(b, request.getFile()));*/
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(Request req) throws IOException {
        Request request = beforeTemplate(req);

        //MultipartBody requestBody = getFilesBody(request.getFormFiles());
        /*使用更全的 ，支持文件和参数一起上传 */

        MultipartBody requestBody = getFilesBody(request.getParams() , request.getFormFiles());

        /*Response response = template(request.getUrl(), Method.POST, request.getContentType(), d -> setRequestBody(d, Method.POST, requestBody), request.getHeaders(),
                request.getConnectionTimeout(), request.getConnectionTimeout(), request.getResultCharset(), request.isIncludeHeaders(),
                Response::with);*/
        Response response = template(request, Method.POST , d -> setRequestBody(d, Method.POST, requestBody) , Response::with);
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
