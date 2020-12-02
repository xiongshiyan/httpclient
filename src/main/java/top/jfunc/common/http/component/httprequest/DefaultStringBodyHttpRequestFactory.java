package top.jfunc.common.http.component.httprequest;

import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.basic.CommonBodyRequest;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

/**
 * @author xiongshiyan at 2020/1/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@Deprecated
public class DefaultStringBodyHttpRequestFactory implements StringBodyHttpRequestFactory {
    @Override
    public StringBodyRequest create(String url,
                                    MultiValueMap<String, String> queryParams,
                                    String body,
                                    String contentType,
                                    MultiValueMap<String, String> headers,
                                    int connectTimeout,
                                    int readTimeout,
                                    String bodyCharset,
                                    String resultCharset) {
        CommonBodyRequest stringBodyRequest = CommonBodyRequest.of(url);
        stringBodyRequest.setBody(body , contentType)
                        .setConnectionTimeout(connectTimeout)
                        .setReadTimeout(readTimeout);

        if(MapUtil.notEmpty(queryParams)){
            stringBodyRequest.setQueryParams(queryParams);
        }

        if(MapUtil.notEmpty(headers)){
            stringBodyRequest.setHeaders(headers);
        }

        if(null != bodyCharset){
            stringBodyRequest.setBodyCharset(bodyCharset);
        }

        if(null != resultCharset){
            stringBodyRequest.setResultCharset(resultCharset);
        }
        return stringBodyRequest;
    }
}
