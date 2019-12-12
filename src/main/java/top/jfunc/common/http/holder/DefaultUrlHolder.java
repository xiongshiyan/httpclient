package top.jfunc.common.http.holder;

import top.jfunc.common.http.ParamUtil;

/**
 * @author xiongshiyan at 2019/6/28 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultUrlHolder implements UrlHolder {
    private String url = null;
    /**
     * 保存全路径[后面所有的参数拼接起来的URL]，相当于缓存
     */
    protected String cacheFinalUrl = null;
    /**
     * 路径参数，形如这种URL http://httpbin.org/book/{id}，保存id和id的值
     * @since 1.0.4 //private Map<String , String> routeParams;
     */
    private RouteParamHolder routeParamHolder = new DefaultRouteParamHolder();
    /**
     * 查询参数，拼装在URL后面 ?//private MultiValueMap<String,String> queryParamHolder;
     * @since 1.0.4
     */
    private ParamHolder queryParamHolder = new DefaultParamHolder();

    /**
     * 获取之后，最好就不要再更改参数了，或者在之前调用{@link PhpUrlHolder#recalculateUrl()}
     * @return 计算后的url
     */
    @Override
    public String getUrl() {
        if(null != cacheFinalUrl){
            return cacheFinalUrl;
        }
        //处理路径参数
        cacheFinalUrl = ParamUtil.replaceRouteParamsIfNecessary(url , routeParamHolder.getMap());
        //处理Query参数
        cacheFinalUrl = ParamUtil.contactUrlParams(cacheFinalUrl , queryParamHolder.getParams() , queryParamHolder.getParamCharset());
        return cacheFinalUrl;
    }

    @Override
    public String recalculateUrl(){
        //finalUrl=null的时候就会重新计算
        cacheFinalUrl = null;
        return getUrl();
    }

    @Override
    public UrlHolder setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String getOriginalUrl(){
        return url;
    }

    @Override
    public RouteParamHolder routeParamHolder() {
        return routeParamHolder;
    }

    @Override
    public ParamHolder queryParamHolder() {
        return queryParamHolder;
    }
}
