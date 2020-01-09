package top.jfunc.common.http.smart.jdk;

import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.HeaderExtractor;
import top.jfunc.common.http.util.NativeUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultJdkHeaderExtractor implements HeaderExtractor<HttpURLConnection> {
    @Override
    public MultiValueMap<String, String> extract(HttpURLConnection connection, HttpRequest httpRequest, String completedUrl) throws IOException {
        ///1.如果支持重定向，必须读取header
        if(httpRequest.followRedirects()){
            //includeHeaders = HttpRequest.INCLUDE_HEADERS;
            httpRequest.setIncludeHeaders(HttpRequest.INCLUDE_HEADERS);
        }
        //2.从响应中获取headers
        MultiValueMap<String, String> responseHeaders = parseResponseHeaders(connection, httpRequest);

        //3.处理cookie
        //saveCookieIfNecessary(httpRequest, completedUrl , responseHeaders);

        return responseHeaders;
    }

    protected MultiValueMap<String , String> parseResponseHeaders(HttpURLConnection connection , HttpRequest httpRequest){
        return NativeUtil.parseHeaders(connection , httpRequest.isIncludeHeaders());
    }
}
