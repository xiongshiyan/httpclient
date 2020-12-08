package top.jfunc.common.http.cookie;

import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.CollectionUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模仿jodd-http的实现
 * @author xiongshiyan at 2020/1/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultCookieAccessor implements CookieAccessor {

    private CookieStore cookieStore;

    public DefaultCookieAccessor(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    /**
     * 请求之前添加{@link Cookie}，{@link Cookie}从{@link CookieStore}中获取
     * @param httpRequest HttpRequest
     * @throws IOException IOException
     */
    @Override
    public void addCookieIfNecessary(HttpRequest httpRequest) throws IOException {
        CookieStore cookieStore = getCookieStore();
        if(null == cookieStore){
            return;
        }

        List<Cookie> cookies = cookieStore.loadForRequest(httpRequest);

        if(CollectionUtil.isEmpty(cookies)){
            return;
        }

        String cookieString = buildCookieString(cookies);
        httpRequest.setHeader(HttpHeaders.COOKIE, cookieString);
    }

    /**
     * 形如 Cookie: user_locale=zh-CN; oschina_new_user=false
     * @param cookies 待用cookie
     * @return 构建的cookie header
     */
    protected String buildCookieString(List<Cookie> cookies) {
        return Cookie.buildCookieString(cookies);
    }

    /**
     * 如果存在{@link Cookie}，将响应的{@link Cookie}保存在{@link CookieStore}中
     * @param httpRequest HttpRequest
     * @param responseHeaders 响应的headers
     * @throws IOException IOException
     */
    @Override
    public void saveCookieIfNecessary(HttpRequest httpRequest, MultiValueMap<String, String> responseHeaders) throws IOException {
        CookieStore cookieStore = getCookieStore();
        if(null == cookieStore || MapUtil.isEmpty(responseHeaders)){
            return;
        }

        List<Cookie> cookies = getCookies(responseHeaders);
        if(CollectionUtil.isEmpty(cookies)){
            return;
        }

        cookieStore.saveFromResponse(cookies, httpRequest);
    }

    protected List<Cookie> getCookies(MultiValueMap<String, String> responseHeaders) {
        List<String> cookieList = getCookies(responseHeaders, HttpHeaders.SET_COOKIE);
        List<String> cookie2List = getCookies(responseHeaders, HttpHeaders.SET_COOKIE2);
        List<String> newCookies = CollectionUtil.merge(cookieList, cookie2List);
        return Cookie.parseCookies(newCookies);
    }

    protected List<String> getCookies(MultiValueMap<String, String> responseHeaders, String cookieHeader) {
        List<String> newCookies = new ArrayList<>(4);
        for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
            if(cookieHeader.equalsIgnoreCase(entry.getKey())){
                newCookies.addAll(entry.getValue());
            }
        }
        return newCookies;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }
}
