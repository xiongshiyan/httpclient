package top.jfunc.common.http.component.httprequest;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.basic.CommonBodyRequest;
import top.jfunc.common.http.request.basic.CommonRequest;
import top.jfunc.common.http.request.basic.UpLoadRequest;
import top.jfunc.common.utils.ArrayUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

/**
 * @author xiongshiyan at 2020/1/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultSimpleFactory implements HttpRequestFactory, StringBodyHttpRequestFactory, UploadRequestFactory {
    @Override
    public HttpRequest create(String url,
                              MultiValueMap<String, String> queryParams,
                              MultiValueMap<String, String> headers,
                              int connectTimeout,
                              int readTimeout,
                              String resultCharset) {
        HttpRequest httpRequest = CommonRequest.of(url);
        set(httpRequest, queryParams, headers, connectTimeout, readTimeout, resultCharset);
        return httpRequest;
    }

    @Override
    public StringBodyRequest create(String url, MultiValueMap<String, String> queryParams, String body, String contentType, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) {
        CommonBodyRequest stringBodyRequest = CommonBodyRequest.of(url);
        stringBodyRequest.setBody(body , contentType);
        if(null != bodyCharset){
            stringBodyRequest.setBodyCharset(bodyCharset);
        }
        set(stringBodyRequest, queryParams, headers, connectTimeout, readTimeout, resultCharset);
        return stringBodyRequest;
    }

    @Override
    public UploadRequest create(String url,
                                MultiValueMap<String, String> formParams,
                                MultiValueMap<String, String> headers,
                                int connectTimeout,
                                int readTimeout,
                                String resultCharset,
                                FormFile... files) {
        UploadRequest uploadRequest = UpLoadRequest.of(url);

        if(MapUtil.notEmpty(formParams)){
            uploadRequest.setFormParams(formParams);
        }
        if(ArrayUtil.isNotEmpty(files)){
            uploadRequest.addFormFile(files);
        }

        set(uploadRequest, null, headers, connectTimeout, readTimeout, resultCharset);

        return uploadRequest;
    }

    protected void set(HttpRequest httpRequest,
                       MultiValueMap<String, String> queryParams,
                       MultiValueMap<String, String> headers,
                       int connectTimeout,
                       int readTimeout,
                       String resultCharset){

        if(MapUtil.notEmpty(queryParams)){
            httpRequest.setQueryParams(queryParams);
        }

        if(MapUtil.notEmpty(headers)){
            httpRequest.setHeaders(headers);
        }

        httpRequest.setConnectionTimeout(connectTimeout).setReadTimeout(readTimeout);

        if(null != resultCharset){
            httpRequest.setResultCharset(resultCharset);
        }
    }
}
