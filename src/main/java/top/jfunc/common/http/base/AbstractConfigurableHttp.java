package top.jfunc.common.http.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.util.Objects;

/**
 * 一些http的公共方法处理
 * @author xiongshiyan at 2018/8/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractConfigurableHttp {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConfigurableHttp.class);

    static {
        logger.info("you can instantiate an implementation class of SmartHttpClient interface AND Do more setting like this:");
        logger.info("\n      smartHttpClient.setConfig(Config.defaultConfig()\n" +
                    "        .setBaseUrl(\"https://fanyi.baidu.com/\")\n" +
                    "        .addDefaultHeader(\"xx\" , \"xx\")\n" +
                    "        .setDefaultBodyCharset(\"UTF-8\")\n" +
                    "        .setDefaultResultCharset(\"UTF-8\")\n" +
                    "        .setDefaultConnectionTimeout(15000)\n" +
                    "        .setDefaultReadTimeout(15000))...");
    }


    private ConfigFrozen configFrozen = new ConfigFrozen();

    private Config config = Config.defaultConfig();

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        configFrozen.ensureConfigNotFreeze();
        this.config = Objects.requireNonNull(config);
    }

    public void freezeConfig() {
        //本身冻结
        configFrozen.freezeConfig();
        //Config冻结
        config.freezeConfig();
    }

    /**
     * 处理基路径和默认Query参数：注意url可能已经带Query参数了
     * @param originUrl 客户设置的url
     * @return 处理后的url
     */
    protected String handleUrlIfNecessary(String originUrl){
        return config.handleUrlIfNecessary(originUrl , null , null , null);
    }


    ///////////////////////////////以下几个方法太长了，提供便捷方式///////////////////////////////////////

    protected HostnameVerifier getDefaultHostnameVerifier() {
        return config.sslHolder().getHostnameVerifier();
    }
    protected SSLContext getDefaultSSLContext() {
        return config.sslHolder().getSslContext();
    }
    protected SSLSocketFactory getDefaultSSLSocketFactory() {
        return config.sslHolder().getSslSocketFactory();
    }
    protected X509TrustManager getDefaultX509TrustManager() {
        return config.sslHolder().getX509TrustManager();
    }
}
