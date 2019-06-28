package top.jfunc.common.http.holder;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.Protocol;

import static top.jfunc.common.http.HttpConstants.*;

/**
 * @author xiongshiyan at 2019/6/28 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultUrlHolder implements UrlHolder {
    /**
     * 保存全路径[后面所有的参数拼接起来的URL]，相当于缓存
     */
    private String finalUrl = null;

    /**
     * 下面三个参数可能都没有
     */
    private Protocol protocol;
    private String host;
    private int port = -1;

    /**
     * 路径，不包括query参数的
     */
    private String path = SPLASH;

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
     * 获取之后，最好就不要再更改参数了，或者在之前调用{@link UrlHolder#recalculate()}
     * @return 计算后的url
     */
    @Override
    public String getUrl() {
        if(null != finalUrl){
            return finalUrl;
        }

        if(null == protocol){
            //说明不是完整的路径
            this.finalUrl = handleUrlIfNecessary(path);
            return this.finalUrl;
        }

        //有协议说明肯定是完整路径
        if(null == host){
            throw new IllegalArgumentException("host 未指定");
        }
        //1.拼装url
        String url = protocol.name().toLowerCase() + COLON_SPLASH + host + COLON + port ;
        url = url + (path.startsWith(SPLASH) ? path : (SPLASH + path));

        finalUrl = handleUrlIfNecessary(url);
        return finalUrl;
    }

    private String handleUrlIfNecessary(String origin){
        //2.处理路径参数
        String routeUrl = ParamUtil.replaceRouteParamsIfNecessary(origin , routeParamHolder.getMap());

        //3.处理Query参数
        return ParamUtil.contactUrlParams(routeUrl, queryParamHolder.getParams() , queryParamHolder.getParamCharset());
    }

    @Override
    public String recalculate(){
        //finalUrl=null的时候就会重新计算
        finalUrl = null;
        return getUrl();
    }

    @Override
    public UrlHolder setUrl(String destination) {
        this.finalUrl = destination;
        // protocol
        int ndx = destination.indexOf(COLON_SPLASH);
        if (ndx != -1) {
            //获取协议，如果协议名不正确，直接抛出异常
            this.protocol = Protocol.valueOf(destination.substring(0, ndx).toUpperCase());
            this.port = protocol.getPort();
            destination = destination.substring(ndx + 3);
        }
        // host
        ndx = destination.indexOf(SPLASH);

        if (ndx == -1) {
            ndx = destination.length();
        }

        if (ndx != 0) {

            String hostToSet = destination.substring(0, ndx);
            destination = destination.substring(ndx);

            // port

            ndx = hostToSet.indexOf(HttpConstants.COLON);

            if (ndx != -1) {
                port = Integer.parseInt(hostToSet.substring(ndx + 1));
                hostToSet = hostToSet.substring(0, ndx);
            }

            this.host = hostToSet;
        }

        // path + query
        this.path = destination;

        //处理query参数
        ndx = destination.indexOf(QUESTION_MARK);
        if(-1 != ndx){
            this.path = destination.substring(0, ndx);
            //k1=v1&k2=v2
            destination = destination.substring(ndx+1);
            String[] kvs = destination.split(AND);
            for (String kv : kvs) {
                String[] split = kv.split(EQUALS);
                queryParamHolder.addParam(split[0] , split[1]);
            }
        }
        return this;
    }

    @Override
    public UrlHolder protocol(Protocol protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    public Protocol protocol() {
        return protocol;
    }

    @Override
    public UrlHolder host(String host) {
        this.host = host;
        return this;
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public UrlHolder port(int port) {
        this.port = port;
        return this;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public UrlHolder path(String path , String... paths) {
        this.path = path;
        if(null != paths && paths.length > 0){
            for (String p : paths) {
                this.path = ParamUtil.addBaseUrlIfNecessary(this.path , p);
            }
        }
        return this;
    }

    @Override
    public String path() {
        return path;
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
