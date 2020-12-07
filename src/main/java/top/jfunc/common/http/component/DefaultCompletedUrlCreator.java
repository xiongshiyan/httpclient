package top.jfunc.common.http.component;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.util.ParamUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.ObjectUtil;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultCompletedUrlCreator implements CompletedUrlCreator {
    /**
     * 处理Route参数、BaseURL、Query参数
     * @param httpRequest 请求
     * @return 处理过后的URL
     */
    @Override
    public String complete(HttpRequest httpRequest) {
        Config config = httpRequest.getConfig();
        Config.throwExIfNull(config);
        //1.处理Route参数
        String routeUrl = ParamUtil.replaceRouteParamsIfNecessary(httpRequest.getUrl() , httpRequest.getRouteParams());
        //2.处理BaseUrl
        String urlWithBase = ParamUtil.concatUrlIfNecessary(config.getBaseUrl() , routeUrl);
        //3.处理Query参数
        MultiValueMap<String, String> params = MapUtil.mergeMap(httpRequest.getQueryParams(), config.getDefaultQueryParams());
        String queryCharsetWithDefault = ObjectUtil.defaultIfNull(httpRequest.getQueryParamCharset() , config.getDefaultQueryCharset());
        return ParamUtil.contactUrlParams(urlWithBase, params, queryCharsetWithDefault);
    }
}
