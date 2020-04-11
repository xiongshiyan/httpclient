package top.jfunc.common.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.common.http.smart.SmartHttpClient;
import top.jfunc.common.utils.ClassUtil;

/**
 * 动态获取SmartHttpClient的实现类，基于jar包检测 <pre>HttpDelegate.delegate()....</pre>
 * @see HttpStatic
 * @author xiongshiyan at 2017/12/11
 */
public class HttpDelegate {
    private static final Logger logger = LoggerFactory.getLogger(HttpDelegate.class);

    private HttpDelegate(){}

    /**根据类路径的jar加载默认顺序是 OKHttp3、ApacheHttpClient、Jodd、URLConnection*/
    private static final String[] SMART_HTTP_CLIENT_CLASSES = {
            "top.jfunc.common.http.smart.OkHttp3SmartHttpClient",
            "top.jfunc.common.http.smart.ApacheSmartHttpClient",
            "top.jfunc.common.http.smart.JoddSmartHttpClient",
            "top.jfunc.common.http.smart.NativeSmartHttpClient"};
    /**
     * http请求工具代理对象
     */
    private static final SmartHttpClient DELEGATE;
    static {
        DELEGATE = initDelegate();
    }

    public static SmartHttpClient delegate() {
        if(null == DELEGATE){
            throw new RuntimeException("SmartHttpClient初始化失败，请使用HttpStatic或者直接实例化");
        }
        return DELEGATE;
    }


    private static SmartHttpClient initDelegate() {
        Class delegateClassToUse = null;
        try {
            for (String smartHttpClientClass : SMART_HTTP_CLIENT_CLASSES) {
                if(ClassUtil.isPresent(HttpDelegate.class.getClassLoader() , smartHttpClientClass)){
                    delegateClassToUse = ClassUtil.loadClass(smartHttpClientClass);
                    break;
                }
            }

            return  null == delegateClassToUse ? null : (SmartHttpClient) delegateClassToUse.newInstance();
        }catch (Exception e){
            logger.warn("自动探测SmartHttpClient失败,请不要使用HttpDelegate,或者引入一种实现" , e);
            return null;
        }
    }
}
