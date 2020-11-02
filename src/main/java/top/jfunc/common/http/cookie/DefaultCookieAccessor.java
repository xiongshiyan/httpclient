package top.jfunc.common.http.cookie;

import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.CollectionUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.StrUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模仿jodd-http的实现
 * @author xiongshiyan at 2020/1/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultCookieAccessor implements CookieAccessor {
    /**
     * @param httpRequest HttpRequest
     * @throws IOException IOException
     */
    @Override
    public void addCookieIfNecessary(HttpRequest httpRequest) throws IOException {
        CookieStore cookieStore = httpRequest.getConfig().getCookieStore();
        if(null == cookieStore){
            return;
        }

        List<Cookie> cookies = cookieStore.loadForRequest(httpRequest);

        if(CollectionUtil.isEmpty(cookies)){
            return;
        }

        StringBuilder cookieString = buildCookieString(cookies);
        httpRequest.setHeader(HttpHeaders.COOKIE, cookieString.toString());
    }

    /**
     * 形如 Cookie: user_locale=zh-CN; oschina_new_user=false
     * @param cookies 待用cookie
     * @return 构建的cookie header
     */
    protected StringBuilder buildCookieString(List<Cookie> cookies) {
        StringBuilder cookieString = new StringBuilder();

        boolean first = true;

        for (Cookie cookie : cookies) {
            Integer maxAge = cookie.getMaxAge();
            if (maxAge != null && maxAge == 0) {
                continue;
            }

            if (!first) {
                cookieString.append("; ");
            }

            first = false;
            cookieString.append(cookie.getName());
            cookieString.append(StrUtil.EQUALS);
            cookieString.append(cookie.getValue());
        }
        return cookieString;
    }

    /**
     * 如果存在Cookie，将响应的Cookie保存起来
     * @param httpRequest HttpRequest
     * @param responseHeaders 响应的headers
     * @throws IOException IOException
     */
    @Override
    public void saveCookieIfNecessary(HttpRequest httpRequest, MultiValueMap<String, String> responseHeaders) throws IOException {
        CookieStore cookieStore = httpRequest.getConfig().getCookieStore();
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
        List<Cookie> cookieList = getCookies(responseHeaders, HttpHeaders.SET_COOKIE);
        List<Cookie> cookie2List = getCookies(responseHeaders, HttpHeaders.SET_COOKIE2);
        return CollectionUtil.merge(cookieList, cookie2List);
    }

    protected List<Cookie> getCookies(MultiValueMap<String, String> responseHeaders, String cookieHeader) {
        List<String> newCookies = new ArrayList<>(4);
        for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
            if(cookieHeader.equalsIgnoreCase(entry.getKey())){
                newCookies.addAll(entry.getValue());
            }
        }

        if (CollectionUtil.isEmpty(newCookies)) {
            return null;
        }

        List<Cookie> cookieList = new ArrayList<>(newCookies.size());

        for (String cookieValue : newCookies) {
            try {
                cookieList.add(new Cookie(cookieValue));
            }catch (Exception e) {
                // ignore
            }
        }
        return cookieList;
    }
}
