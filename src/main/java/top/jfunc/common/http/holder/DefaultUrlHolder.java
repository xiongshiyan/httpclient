package top.jfunc.common.http.holder;

/**
 * @author xiongshiyan at 2019/6/28 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultUrlHolder implements UrlHolder {
    private String url = null;
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

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public UrlHolder setUrl(String url) {
        this.url = url;
        return this;
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
