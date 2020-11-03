package top.jfunc.common.http.paramsign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.common.http.base.Method;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.StrUtil;

import java.io.IOException;

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
public abstract class AbstractParamSigner<R> implements ParamSigner<R> {
    private static final Logger logger     = LoggerFactory.getLogger(AbstractParamSigner.class);

    @Override
    public void validIfNecessary(R r) throws IOException{
        SignParam signParam = checkSignParam(r);
        if(null == signParam){
            return;
        }
        valid(r , signParam);
    }

    protected void valid(R r , SignParam signParam) throws IOException {
        //校验是否有必要的参数
        validHasParam(r, signParam);

        //校验时间戳，目前要求以服务器时间前后60秒
        validTimeStamp(r , signParam);

        //校验请求参数
        validRequest(r, signParam);
    }

    protected void validHasParam(R r , SignParam signParam) {
        if (StrUtil.isEmpty(signParam.getTimeStamp())
                || StrUtil.isEmpty(signParam.getNonceStr())
                || StrUtil.isEmpty(signParam.getSign())) {
            logger.info("参数为空:" + signParam.getPath());
            throw new ParamSignException("参数异常,请升级APP重试" , signParam);
        }
    }

    protected void validTimeStamp(R r , SignParam signParam) {
        String ts = signParam.getTimeStamp();
        long timestamp = Long.parseLong(ts);
        long now = System.currentTimeMillis();
        //if(ts >= now || now - ts > PARAM_SIGN_INTERVAL){
        //因为客户端时间可能不准，给一个裕量
        long diff = now - timestamp;
        long abs = Math.abs(diff);
        if (abs > getParamSignInterval()) {
            logger.info(signParam.getPath() + ":" + now + "-" + ts + "=" + diff);
            throw new ParamSignException("时间校验失败：您的手机时间大约"+(diff>0?"慢":"快")+"了"  + abs/1000 + "秒，请调整后重试；如果手机时间准确，可能是网络延迟太大，请稍后重试。" , signParam);
        }
    }

    protected void validRequest(R r, SignParam signParam) throws IOException{
        //校验GET请求，Query参数参与签名
        validGet(r, signParam);

        //校验POST请求，分Form和其他的含有body的（主要是json），Form所有参数参与签名，body类型的使用param=md5(body)参与签名
        validPost(r, signParam);
    }

    protected void validGet(R r, SignParam signParam) throws IOException{
        if (Method.GET.name().equalsIgnoreCase(signParam.getMethod())) {
            MultiValueMap<String, String> paramMap = mappedParamForGet(r);
            validParam(paramMap, signParam);
        }
    }

    protected void validPost(R r, SignParam signParam) throws IOException {
        if (Method.POST.name().equalsIgnoreCase(signParam.getMethod())) {
            MultiValueMap<String, String> paramMap = mappedParamForPost(r);
            validParam(paramMap, signParam);
        }
    }


    protected void validParam(MultiValueMap<String, String> paramMap, SignParam signParam) {
        MultiValueMap<String, String> handleMap = handleMap(paramMap, signParam);

        String signStr = getSignStr(handleMap, signParam);

        String signToJudge = doSign(signStr, signParam);

        if (!signToJudge.equals(signParam.getSign())) {
            logger.info(signParam.getPath() + ":" + signStr + " -> " + signToJudge + " ?= " + signParam.getSign());
            throw new ParamSignException("参数签名异常，请升级APP重试" , signParam);
        }
    }

    protected MultiValueMap<String, String> handleMap(MultiValueMap<String, String> paramMap, SignParam signParam){
        return ParamSignUtil.handleMap(paramMap, signParam.getTimeStamp(), signParam.getNonceStr());
    }
    protected String getSignStr(MultiValueMap<String, String> paramMap, SignParam signParam) {
        return ParamSignUtil.getSignStr(paramMap);
    }

    /**
     * 获取请求用于签名的参数，可以放在header中，可以拼成一个字段放在header中，同样可以放在Query中
     * @param r 代表请求
     * @return SignParam 用于参数签名的参数。如果此方法返回null，则不进行参数签名，可以在此方法中判断是否需要参数签名
     * @throws IOException IOException
     */
    protected abstract SignParam checkSignParam(R r) throws IOException;
    /**
     * 时间戳裕量
     */
    protected long getParamSignInterval(){return 60000;}
    /**
     * 将post请求的body组装成一个map，一般分为POST|x-www-form-urlencoded、POST|json
     * @param r 代表请求
     * @return MultiValueMap POST参数map
     * @throws IOException IOException
     */
    protected abstract MultiValueMap<String, String> mappedParamForPost(R r) throws IOException;
    /**
     * 将get请求的query组装成一个map
     * @param r 代表请求
     * @return MultiValueMap GET参数map
     * @throws IOException IOException
     */
    protected abstract MultiValueMap<String, String> mappedParamForGet(R r) throws IOException;
    /**
     * 签名方法，具体的签名方法
     */
    protected abstract String doSign(String toSign, SignParam signParam);
}
