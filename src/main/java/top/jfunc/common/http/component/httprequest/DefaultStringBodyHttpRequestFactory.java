package top.jfunc.common.http.component.httprequest;

import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.basic.CommonBodyRequest;

import java.util.Map;

/**
 * @author xiongshiyan at 2020/1/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultStringBodyHttpRequestFactory implements StringBodyHttpRequestFactory {
    @Override
    public StringBodyRequest create(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) {
        CommonBodyRequest stringBodyRequest = CommonBodyRequest.of(url);
        stringBodyRequest.setBody(body , contentType)
                .setBodyCharset(bodyCharset)
                .setHeaders(headers)
                .setConnectionTimeout(connectTimeout)
                .setReadTimeout(readTimeout);

        if(null != resultCharset){
            stringBodyRequest.setResultCharset(resultCharset);
        }
        return stringBodyRequest;
    }
}
