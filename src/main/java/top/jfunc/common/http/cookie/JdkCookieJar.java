package top.jfunc.common.http.cookie;

import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * 使用JDK提供的 {@link CookieHandler} 实现Cookie管理
 * @see CookieHandler
 * @see CookieManager
 * @author xiongshiyan at 2019/12/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class JdkCookieJar implements CookieJar{
    private CookieHandler cookieHandler;

    public JdkCookieJar(CookieHandler cookieHandler , boolean settingDefault) {
        setCookieHandler(cookieHandler , settingDefault);
    }
    public JdkCookieJar(CookieHandler cookieHandler) {
        setCookieHandler(cookieHandler);
    }

    public JdkCookieJar() {
    }

    public void setCookieHandler(CookieHandler cookieHandler) {
        this.cookieHandler = cookieHandler;
    }

    /**
     * @see CookieHandler#setDefault(CookieHandler)
     * @see CookieHandler#getDefault()
     * @param cookieHandler CookieHandler
     * @param settingDefault 是否设置默认的CookieHandler
     */
    public void setCookieHandler(CookieHandler cookieHandler , boolean settingDefault) {
        this.cookieHandler = cookieHandler;
        if(settingDefault && null == CookieHandler.getDefault()){
            CookieHandler.setDefault(cookieHandler);
        }
    }

    public CookieHandler getCookieHandler() {
        return cookieHandler;
    }

    @Override
    public List<String> loadForRequest(String completedUrl , MultiValueMap<String , String> requestHeaders) throws IOException{
        //从源码知道CookieManager#get方法传入的Map基本没用，不为空即可，不知道这样设计干嘛的
        MultiValueMap<String, String> nonNull = null != requestHeaders ? requestHeaders : new ArrayListMultiValueMap<>(0);
        Map<String, List<String>> cookies = getCookieHandler().get(URI.create(completedUrl), nonNull);
        if(MapUtil.notEmpty(cookies)){
            return cookies.get(HttpHeaders.COOKIE);
        }
        return null;
    }

    @Override
    public void saveFromResponse(String completedUrl, MultiValueMap<String, String> responseHeaders) throws IOException{
        if(null != getCookieHandler() && MapUtil.notEmpty(responseHeaders)){
            CookieHandler cookieHandler = getCookieHandler();
            cookieHandler.put(URI.create(completedUrl) , responseHeaders);
        }
    }
}
