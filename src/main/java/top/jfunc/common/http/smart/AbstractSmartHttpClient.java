package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.basic.AbstractHttpClient;
import top.jfunc.common.http.request.*;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;

/**
 * 实现者只需要实现HttpTemplate接口、处理POST Body、文件上传Body即可
 * @see SmartHttpClient
 * @see AbstractSmartHttpClient#bodyContentCallback(String, String, String)
 * @see AbstractSmartHttpClient#uploadContentCallback(MultiValueMap, FormFile[])
 * @author xiongshiyan at 2019/5/8 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractSmartHttpClient<CC> extends AbstractHttpClient<CC> implements SmartHttpClient, SmartHttpTemplate<CC> {

    @Override
    public Response get(HttpRequest req) throws IOException {
        HttpRequest request = beforeTemplate(req);
        Response response = template(request, Method.GET , null , Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response post(StringBodyRequest req) throws IOException {
        StringBodyRequest request = beforeTemplate(req);
        String body = request.getBody();
        Response response = template(request, Method.POST ,
                bodyContentCallback(body, getBodyCharsetWithDefault(request.getBodyCharset()) , request.getContentType()) ,
                Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response httpMethod(HttpRequest req, Method method) throws IOException {
        HttpRequest request = beforeTemplate(req);
        ContentCallback<CC> contentCallback = null;
        if(method.hasContent() && request instanceof StringBodyRequest){
            String body = ((StringBodyRequest)request).getBody();
            final String bodyCharset = CharsetUtil.bodyCharsetFromRequest(request);
            contentCallback = bodyContentCallback(body, getBodyCharsetWithDefault(bodyCharset) , request.getContentType());
        }
        Response response = template(request, method , contentCallback, Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public byte[] getAsBytes(HttpRequest req) throws IOException {
        HttpRequest request = beforeTemplate(req);
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File download(DownloadRequest req) throws IOException {
        DownloadRequest request = beforeTemplate(req);
        return template(request , Method.GET, null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(UploadRequest req) throws IOException {
        UploadRequest request = beforeTemplate(req);
        Response response = template(request , Method.POST ,
                uploadContentCallback(request.getFormParams(), request.getFormFiles()) , Response::with);
        return afterTemplate(request , response);
    }
}

