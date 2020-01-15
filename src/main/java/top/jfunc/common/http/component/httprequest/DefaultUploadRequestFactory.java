package top.jfunc.common.http.component.httprequest;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.basic.UpLoadRequest;
import top.jfunc.common.utils.MultiValueMap;

/**
 * @author xiongshiyan at 2020/1/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultUploadRequestFactory implements UploadRequestFactory {
    @Override
    public UploadRequest create(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) {
        UploadRequest uploadRequest = UpLoadRequest.of(url);
        uploadRequest.addFormFile(files)
                .setConnectionTimeout(connectTimeout)
                .setReadTimeout(readTimeout);

        uploadRequest.setFormParams(params);
        uploadRequest.setHeaders(headers);

        if(null != resultCharset){
            uploadRequest.setResultCharset(resultCharset);
        }

        return uploadRequest;
    }
}
