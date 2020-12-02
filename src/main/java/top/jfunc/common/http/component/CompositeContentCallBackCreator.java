package top.jfunc.common.http.component;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.UploadRequest;

import java.io.IOException;

/**
 * @see top.jfunc.common.http.smart.AbstractSmartHttpClient
 * @author xiongshiyan at 2020/10/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CompositeContentCallBackCreator<CC> implements ContentCallbackCreator<CC> {
    private BodyContentCallbackCreator<CC> bodyContentCallbackCreator;
    private UploadContentCallbackCreator<CC> uploadContentCallbackCreator;


    public CompositeContentCallBackCreator(BodyContentCallbackCreator<CC> bodyContentCallbackCreator,
                                           UploadContentCallbackCreator<CC> uploadContentCallbackCreator) {
        this.bodyContentCallbackCreator = bodyContentCallbackCreator;
        this.uploadContentCallbackCreator = uploadContentCallbackCreator;
    }

    @Override
    public ContentCallback<CC> create(HttpRequest httpRequest) throws IOException {
        if(httpRequest instanceof UploadRequest){
            return getUploadContentCallbackCreator().create(httpRequest);
        }
        return getBodyContentCallbackCreator().create(httpRequest);
    }

    public BodyContentCallbackCreator<CC> getBodyContentCallbackCreator() {
        return bodyContentCallbackCreator;
    }

    public UploadContentCallbackCreator<CC> getUploadContentCallbackCreator() {
        return uploadContentCallbackCreator;
    }
}
