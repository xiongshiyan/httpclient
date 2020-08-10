package top.jfunc.common.http.paramsign;

/**
 * @author xiongshiyan at 2020/8/10 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultSignParam implements SignParam{
    /**
     * 路径
     */
    private final String path;
    /**
     * 传递过来的时间戳
     */
    private final String ts;
    /**
     * 传递过来的随机串
     */
    private final String nonceStr;
    /**
     * 传递过来的签名
     */
    private final String sign;

    public DefaultSignParam(String path, String ts, String nonceStr, String sign) {
        this.path = path;
        this.ts = ts;
        this.nonceStr = nonceStr;
        this.sign = sign;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getTimeStamp() {
        return ts;
    }

    @Override
    public String getNonceStr() {
        return nonceStr;
    }

    @Override
    public String getSign() {
        return sign;
    }
}
