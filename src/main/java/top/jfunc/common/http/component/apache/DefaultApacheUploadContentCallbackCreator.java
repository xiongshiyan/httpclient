package top.jfunc.common.http.component.apache;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.component.AbstractUploadContentCallbackCreator;
import top.jfunc.common.http.util.ApacheUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheUploadContentCallbackCreator extends AbstractUploadContentCallbackCreator<HttpRequest> {
    @Override
    public ContentCallback<HttpRequest> create(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        return request -> upload(request, params , paramCharset , formFiles);
    }

    protected void upload(HttpRequest request, MultiValueMap<String, String> params , String charset , Iterable<FormFile> formFiles) throws IOException{
        if(!(request instanceof HttpEntityEnclosingRequest)){
            return;
        }

        ApacheUtil.upload0((HttpEntityEnclosingRequest)request, params, charset, formFiles);
    }
}
