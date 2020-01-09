package top.jfunc.common.http.component.apache;

import org.apache.http.HttpEntityEnclosingRequest;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.component.AbstractUploadContentCallbackCreator;
import top.jfunc.common.http.util.ApacheUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultApacheUploadContentCallbackCreator extends AbstractUploadContentCallbackCreator<HttpEntityEnclosingRequest> {
    @Override
    public ContentCallback<HttpEntityEnclosingRequest> create(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        return request -> ApacheUtil.upload0(request, params , paramCharset , formFiles);
    }
}
