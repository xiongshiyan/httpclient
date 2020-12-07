package top.jfunc.common.http.component.okhttp3;

import okhttp3.MultipartBody;
import okhttp3.Request;
import top.jfunc.common.http.base.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.component.AbstractUploadContentCallbackCreator;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.util.OkHttp3Util;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultOkHttp3UploadContentCallbackCreator extends AbstractUploadContentCallbackCreator<Request.Builder> {
    @Override
    protected ContentCallback<Request.Builder> doCreate(UploadRequest uploadRequest) throws IOException {
        MultipartBody filesBody = OkHttp3Util.filesBody(uploadRequest.getFormParams() , uploadRequest.getFormFiles());
        return builder -> OkHttp3Util.setRequestBody(builder, Method.POST , filesBody);
    }
}
