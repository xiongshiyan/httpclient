package top.jfunc.common.http.paramsign;

/**
 * @author xiongshiyan at 2020/8/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface SignParam{
    /**
     * 获取请求方法
     */
    String getMethod();

    /**
     * 获取请求路径
     */
    String getPath();

    /**
     * 获取时间戳
     */
    String getTimeStamp();

    /**
     * 获取随机串
     */
    String getNonceStr();

    /**
     * 获取签名方法
     */
    String getSignMethod();

    /**
     * 获取签名串
     */
    String getSign();

}
