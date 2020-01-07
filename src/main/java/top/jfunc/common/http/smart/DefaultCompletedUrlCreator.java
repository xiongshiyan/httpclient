package top.jfunc.common.http.smart;

import top.jfunc.common.http.request.HttpRequest;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultCompletedUrlCreator implements CompletedUrlCreator {
    @Override
    public String complete(HttpRequest httpRequest) {
        return httpRequest.getConfig().handleUrlIfNecessary(httpRequest.getUrl() , httpRequest.getRouteParams() , httpRequest.getQueryParams() , httpRequest.getQueryParamCharset());
    }
}
