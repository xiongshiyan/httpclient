package top.jfunc.common.http.component;

import top.jfunc.common.http.HttpException;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.UploadRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractUploadContentCallbackCreator<CC> implements ContentCallbackCreator<CC> {
    @Override
    public ContentCallback<CC> create(HttpRequest httpRequest) throws IOException{
        if(!(httpRequest instanceof UploadRequest)){
            throw new HttpException("httpRequest is not UploadRequest");
        }
        UploadRequest uploadRequest = (UploadRequest) httpRequest;
        return doCreate(uploadRequest);
    }

    /**
     * 真正的创建方法
     * @param uploadRequest UploadRequest
     * @return ContentCallback
     * @throws IOException IOException
     */
    protected abstract ContentCallback<CC> doCreate(UploadRequest uploadRequest) throws IOException;
}
