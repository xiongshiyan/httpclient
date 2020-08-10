package top.jfunc.common.http.paramsign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.common.http.base.Method;
import top.jfunc.common.utils.ArrayUtil;
import top.jfunc.common.utils.StrUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * 参数验签：包括时间戳和签名验证，参考微信支付宝的
 * https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=4_3
 * https://opendocs.alipay.com/open/291/105974
 *
 * 1、分GET、POST|x-www-form-urlencoded、POST|json（代表有一个body的，其他的类型也如此）等
 * 2、对于前两者可以看做key-value键值对，加入时间戳ts、随机串noncestr，按照字典序排序后md5
 * 3、后者对整个body进行md5，并用param作为key，加入时间戳ts、随机串noncestr，按照字典序排序后md5
 * 4、md5后的值作为签名传递，同时传递ts、noncestr，原参数保持不变
 * 5、后端校验的时候首先校验时间戳ts，后再校验签名
 *
 * @author xiongshiyan at 2020/7/20 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractParamSigner<R> implements ParamSigner<R>{
    private static final Logger logger     = LoggerFactory.getLogger(AbstractParamSigner.class);
    private static final String NULL       = "null";
    private static final String UNDEFINED  = "undefined";
    private static final String TS         = "ts";
    private static final String NONCE_STR  = "noncestr";


    @Override
    public void validIfNecessary(R r) throws IOException{
        if (!isParamSignEnable()) {
            return;
        }
        String path = getPath(r);
        if (ArrayUtil.contains(getParamSignSkipUri(), path)) {
            return;
        }

        valid(r , path);
    }

    protected void valid(R r , String path) throws IOException {
        SignParam signParam = getSignParam(r);

        if (StrUtil.isEmpty(signParam.getTimeStamp())
                || StrUtil.isEmpty(signParam.getNonceStr())
                || StrUtil.isEmpty(signParam.getSign())) {
            throw new ParamSignException("参数异常" , signParam);
        }


        //校验时间戳，目前要求以服务器时间前后60秒
        validTimeStamp(path , signParam);

        //校验GET请求，Query参数参与签名
        validGet(r, signParam);

        //校验POST请求，分Form和其他的含有body的（主要是json），Form所有参数参与签名，body类型的使用param=md5(body)参与签名
        validPost(r, signParam);
    }

    protected void validTimeStamp(String path , SignParam signParam) {
        String ts = signParam.getTimeStamp();
        long timestamp = Long.parseLong(ts);
        long now = System.currentTimeMillis();
        //if(ts >= now || now - ts > PARAM_SIGN_INTERVAL){
        //因为客户端时间可能不准，给一个裕量
        long diff = now - timestamp;
        long abs = Math.abs(diff);
        if (abs > getParamSignInterval()) {
            logger.info(path + ":" + now + "-" + ts + "=" + diff);
            throw new ParamSignException("时间校验失败：您的手机时间大约"+(diff>0?"慢":"快")+"了"  + abs/1000 + "秒，请调整后重试；如果手机时间准确，可能是网络延迟太大，请稍后重试。" , signParam);
        }
    }

    protected void validPost(R r, SignParam signParam) throws IOException {
        if (Method.POST.name().equalsIgnoreCase(getMethod(r))) {
            Map<String, String> paramMap = mappedParamForPost(r);
            validParam(signParam, paramMap);
        }
    }

    protected void validGet(R r, SignParam signParam) throws IOException{
        if (Method.GET.name().equalsIgnoreCase(getMethod(r))) {
            Map<String, String> paramMap = mappedParamForGet(r);
            validParam(signParam, paramMap);
        }
    }


    protected void validParam(SignParam signParam, Map<String, String> paramMap) {
        handleMap(paramMap , signParam);
        String[] signStr = getSign(paramMap, signParam);
        String signToPrint = signStr[0];
        String signToJudge = signStr[1];

        if (!signToJudge.equals(signParam.getSign())) {
            logger.info(signParam.getPath() + ":" + signToPrint + " -> " + signToJudge + " ?= " + signParam.getSign());
            throw new ParamSignException("参数签名异常" , signParam);
        }
    }

    protected boolean removeAble(Map.Entry<String, String> entry) {
        String value = entry.getValue();
        return StrUtil.isEmpty(value)
                || NULL.equalsIgnoreCase(value)
                || UNDEFINED.equalsIgnoreCase(value);
    }

    /**
     * 字典序排序加上keysecret
     */
    protected String[] getSign(Map<String, String> paramMap, SignParam signParam) {
        Object[] keys = paramMap.keySet().toArray();
        Arrays.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (Object key : keys) {
            sb.append(key).append(StrUtil.EQUALS).append(paramMap.get(String.valueOf(key))).append(StrUtil.AND);
        }
        //去掉最后的&
        sb.deleteCharAt(sb.length()-1);
        //打印日志不打印出来key
        String withoutSecretKey = sb.toString();

        String sign = doSign(signParam , withoutSecretKey);
        return new String[]{withoutSecretKey, sign};
    }

    /**
     * 是否开启签名
     */
    protected boolean isParamSignEnable(){return true;}
    /**
     * 跳过的uri
     */
    protected String[] getParamSignSkipUri(){return new String[0];}
    /**
     * 时间戳裕量
     */
    protected long getParamSignInterval(){return 60000;}
    /**
     * 获取请求路径
     */
    protected abstract String getPath(R r);
    /**
     * 获取请求方法
     */
    protected abstract String getMethod(R r);
    /**
     * 获取请求用于签名的参数，可以放在header中，可以拼成一个字段放在header中，同样可以放在Query中
     */
    protected abstract SignParam getSignParam(R r);
    /**
     * 将post请求的body组装成一个map，一般分为POST|x-www-form-urlencoded、POST|json
     */
    protected abstract Map<String, String> mappedParamForPost(R r) throws IOException;
    /**
     * 将get请求的query组装成一个map
     */
    protected abstract Map<String, String> mappedParamForGet(R r) throws IOException;
    /**
     * 可以进一步对map进行处理，比如把secret放进去一起排序
     */
    protected void handleMap(Map<String, String> paramMap, SignParam signParam){
        paramMap.entrySet().removeIf(this::removeAble);
        paramMap.put(TS, signParam.getTimeStamp());
        paramMap.put(NONCE_STR, signParam.getNonceStr());
    }
    /**
     * 签名方法
     */
    protected abstract String doSign(SignParam signParam , String toSign);
}
