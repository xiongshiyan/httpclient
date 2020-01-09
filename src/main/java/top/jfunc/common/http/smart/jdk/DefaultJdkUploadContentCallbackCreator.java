package top.jfunc.common.http.smart.jdk;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.smart.AbstractUploadContentCallbackCreator;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.net.HttpURLConnection;

import static top.jfunc.common.http.util.NativeUtil.upload0;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJdkUploadContentCallbackCreator extends AbstractUploadContentCallbackCreator<HttpURLConnection> {
    @Override
    public ContentCallback<HttpURLConnection> create(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        return connect -> upload0(connect , params , paramCharset , formFiles);
    }
}
