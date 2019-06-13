package top.jfunc.common.http.request;

import top.jfunc.common.http.HttpConstants;

/**
 * @author xiongshiyan at 2019/6/13 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CharsetUtil {
    /**
     * 从Request中获取bodyCharset
     * @param httpRequest HttpRequest
     * @return charset
     */
    public static String bodyCharsetFromRequest(HttpRequest httpRequest){
        if(httpRequest instanceof StringBodyRequest){
            return ((StringBodyRequest)httpRequest).getBodyCharset();
        }
        return HttpConstants.DEFAULT_CHARSET;
    }
}
