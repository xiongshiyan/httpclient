package top.jfunc.common.http.smart;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.UploadRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractUploadContentCallbackCreator<CC> implements UploadContentCallbackCreator<CC>{
    @Override
    public ContentCallback<CC> create(HttpRequest httpRequest, Method method) throws IOException{
        UploadRequest uploadRequest = (UploadRequest) httpRequest;
        return create(uploadRequest.getFormParams() , uploadRequest.getParamCharset() , uploadRequest.getFormFiles());
    }
}
