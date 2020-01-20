package top.jfunc.common.http.component.httprequest;

import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.basic.CommonRequest;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

/**
 * @author xiongshiyan at 2020/1/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultHttpRequestFactory implements HttpRequestFactory {
    @Override
    public HttpRequest create(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset) {
        HttpRequest httpRequest = CommonRequest.of(url);
        httpRequest.setConnectionTimeout(connectTimeout)
                    .setReadTimeout(readTimeout);

        if(MapUtil.notEmpty(params)){
            httpRequest.setQueryParams(params);
        }
        if(MapUtil.notEmpty(headers)){
            httpRequest.setHeaders(headers);
        }
        if(null != resultCharset){
            httpRequest.setResultCharset(resultCharset);
        }
        return httpRequest;
    }
}
