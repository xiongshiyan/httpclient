package top.jfunc.common.http.base;

import java.net.Proxy;

/**
 * 代理设置的相关信息
 * @author xiongshiyan at 2019/5/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ProxyInfo {
    /**
     * Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port));
     */
    private Proxy proxy;
    private String proxyUserName;
    private String proxyPassword;

    public ProxyInfo(Proxy proxy) {
        this.proxy = proxy;
    }

    public static ProxyInfo of(Proxy proxy){
        return new ProxyInfo(proxy);
    }
    public static ProxyInfo of(Proxy proxy , String proxyUserName , String proxyPassword){
        ProxyInfo proxyInfo = of(proxy);
        proxyInfo.setProxyUserName(proxyUserName);
        proxyInfo.setProxyPassword(proxyPassword);
        return proxyInfo;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }
}
