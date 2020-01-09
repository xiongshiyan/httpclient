package top.jfunc.common.http.component.okhttp3;

import okhttp3.MultipartBody;
import okhttp3.Request;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.component.AbstractUploadContentCallbackCreator;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

import static top.jfunc.common.http.util.OkHttp3Util.filesBody;
import static top.jfunc.common.http.util.OkHttp3Util.setRequestBody;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3UploadContentCallbackCreator extends AbstractUploadContentCallbackCreator<Request.Builder> {
    @Override
    public ContentCallback<Request.Builder> create(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        MultipartBody filesBody = filesBody(params , formFiles);
        return builder -> setRequestBody(builder, Method.POST , filesBody);
    }
}
