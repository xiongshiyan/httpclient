package top.jfunc.common.http.smart.jodd;

import jodd.http.HttpRequest;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.smart.AbstractUploadContentCallbackCreator;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

import static top.jfunc.common.http.util.JoddUtil.upload0;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJoddUploadContentCallbackCreator extends AbstractUploadContentCallbackCreator<HttpRequest> {
    @Override
    public ContentCallback<HttpRequest> create(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        return httpRequest -> upload0(httpRequest , params , paramCharset , formFiles);
    }
}
