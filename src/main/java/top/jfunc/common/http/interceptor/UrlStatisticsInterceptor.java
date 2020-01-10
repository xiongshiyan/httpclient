package top.jfunc.common.http.interceptor;

import top.jfunc.common.http.request.HttpRequest;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 统计系统会访问哪些URL
 * @author xiongshiyan at 2019/12/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class UrlStatisticsInterceptor extends InterceptorAdapter {
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
    private Set<String> statisticsUrls = new LinkedHashSet<>();

    public UrlStatisticsInterceptor(boolean containsParams , boolean starting) {
        this.containsParams = containsParams;
        this.starting       = starting;
    }
    public UrlStatisticsInterceptor(boolean containsParams) {
        this.containsParams = containsParams;
    }
    public UrlStatisticsInterceptor() {
    }

    @Override
    public HttpRequest onBefore(HttpRequest httpRequest) {
        if(starting){
            String url = getSaveUrl(httpRequest);
            saveUrl(url);
        }
        return super.onBefore(httpRequest);
    }

    /**
     * 留给子类重写，可以自己对URL进行自定义处理，比如保存到redis
     * @param url 将要处理的URL
     */
    protected void saveUrl(String url) {
        statisticsUrls.add(url);
    }

    protected String getSaveUrl(HttpRequest httpRequest){
        String originUrl = httpRequest.getUrl();
        //不包含参数的情况下，就统计原始URL
        if(!containsParams){
            return originUrl;
        }

        return httpRequest.getCompletedUrl();
    }

    public void start(){
        setStarting(true);
    }
    public void stop(){
        setStarting(false);
    }


    public boolean isContainsParams() {
        return containsParams;
    }

    public void setContainsParams(boolean containsParams) {
        this.containsParams = containsParams;
    }

    public boolean isStarting() {
        return starting;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }

    public Set<String> getStatisticsUrls() {
        return statisticsUrls;
    }
}
