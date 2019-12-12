package top.jfunc.common.http.interceptor;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.request.HttpRequest;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 统计系统会访问哪些URL
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class UrlStatisticInterceptor extends InterceptorAdapter{
    /**
     * URL是否包含Query参数/Route参数，建议设置为false，否则可能会有大量的URL
     */
    private boolean containsParams = false;
    /**
     * 是否开始统计
     */
    private boolean starting = false;
    /**
     * 保存url
     */
    private Set<String> urls = new LinkedHashSet<>();

    public UrlStatisticInterceptor(boolean containsParams) {
        this.containsParams = containsParams;
    }
    public UrlStatisticInterceptor(boolean containsParams , boolean starting) {
        this.containsParams = containsParams;
        this.starting       = starting;
    }
    public UrlStatisticInterceptor() {
    }

    @Override
    public HttpRequest onBefore(HttpRequest httpRequest, Method method) {
        if(starting){
            String url = evictParamsIfNecessary(httpRequest);
            urls.add(url);
        }
        return super.onBefore(httpRequest, method);
    }

    private String evictParamsIfNecessary(HttpRequest httpRequest){
        if(containsParams){
            return httpRequest.getUrl();
        }
        //不包含路径参数和查询参数
        String originalUrl = httpRequest.getOriginalUrl();
        if(originalUrl.contains(HttpConstants.QUESTION_MARK)){
            return originalUrl.substring(0, originalUrl.indexOf(HttpConstants.QUESTION_MARK));
        }
        return originalUrl;
    }

    public void start(){
        starting = true;
    }
    public void stop(){
        starting = false;
    }

    public Set<String> getUrls() {
        return urls;
    }
}
