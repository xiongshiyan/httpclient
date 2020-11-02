package top.jfunc.common.http.cookie;

import top.jfunc.common.http.request.HttpRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiongshiyan at 2020/11/2 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class InMemoryCookieStore implements CookieStore{
    private Map<String, Cookie> cookieMap = new HashMap<>();
    @Override
    public List<Cookie> loadForRequest(HttpRequest httpRequest) throws IOException {
        return new ArrayList<>(cookieMap.values());
    }

    @Override
    public void saveFromResponse(List<Cookie> cookies, HttpRequest httpRequest) throws IOException {
        for (Cookie cookie : cookies) {
            cookieMap.put(cookie.getName(), cookie);
        }
    }
}
